import { existsSync, lstatSync, mkdirSync, readdirSync, rmSync, writeFileSync } from 'fs';
import { compileFromFile } from 'json-schema-to-typescript';
import path from 'path';

const RESOURCES_ROOT = 'src/main/resources/json';
const GENERATED_DIST = 'build/generated/sources/js2ts';

rmSync(GENERATED_DIST, { force: true, recursive: true });

await (async function main({ source, target }) {
    if (existsSync(source)) {
        const filenames = readdirSync(source);

        for (const filename of filenames) {
            const filepath = path.join(source, filename);
            const stats = lstatSync(filepath);

            if (stats.isFile()) {
                const { name } = path.parse(filepath);
                const destinationFilename = path.join(target, `${name}.d.ts`);

                const dependencies = [];
                const compiledSchema = await compileFromFile(filepath, {
                    cwd: source,
                    $refOptions: {
                        resolve: {
                            file: {
                                read: ({ url, extension }) => {
                                    const importPath =
                                        path.relative(source, url)
                                            .split(path.sep)
                                            .join(path.posix.sep)
                                            .replace(extension, '');
                                    const importName = path.parse(url).name;

                                    dependencies.push({
                                        path: `${importPath.startsWith('..') ? '' : './'}${importPath}`,
                                        name: importName
                                    });
                                    return `{ "tsType": "${importName}" }`;
                                },
                            },
                        },
                    },
                });

                dependencies.sort((prev, next) => {
                    const prevPath = prev.path?.toUpperCase() ?? '';
                    const nextPath = next.path?.toUpperCase() ?? '';

                    return prevPath.localeCompare(nextPath);
                });

                const [ firstLine, ...restOfTheFile ] = compiledSchema.split(/\r?\n/);

                const fileContent = [
                    firstLine,
                    ...dependencies.map(({ name, path }) => `import { ${name} } from '${path}';`),
                    '',
                    ...restOfTheFile,
                ].join('\n');

                mkdirSync(target, { recursive: true });
                writeFileSync(destinationFilename, fileContent);
            } else if (stats.isDirectory()) {
                await main({ source: filepath, target: path.join(target, filename) });
            }
        }
    }
})({
    source: RESOURCES_ROOT,
    target: GENERATED_DIST,
});
