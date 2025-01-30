import js from '@eslint/js';
import eslintPluginReact from 'eslint-plugin-react';
import eslintPluginReactHooks from 'eslint-plugin-react-hooks';
import eslintPluginReactRefresh from 'eslint-plugin-react-refresh';
import eslintPluginSimpleImportSort from 'eslint-plugin-simple-import-sort';
import globals from 'globals';
import tseslint from 'typescript-eslint';

export default tseslint.config(
    { ignores: ['dist'] },
    {
        extends: [js.configs.recommended, ...tseslint.configs.recommended],
        files: ['**/*.{ts,tsx}'],
        languageOptions: {
            ecmaVersion: 2020,
            globals: { ...globals.browser },
            parserOptions: {
                ecmaVersion: 'latest',
                ecmaFeatures: {
                    jsx: true,
                },
                sourceType: 'module',
            },
        },
        plugins: {
            'react': eslintPluginReact,
            'react-hooks': eslintPluginReactHooks,
            'react-refresh': eslintPluginReactRefresh,
        },
        rules: {
            ...reactHooks.configs.recommended.rules,
            'react-refresh/only-export-components': [
                'warn',
                { allowConstantExport: true },
            ],
        },
    },
)


// export default [
//     {
//         settings: {
//             react: {
//                 version: '18.3',
//             },
//             'import/resolver': {
//                 alias: {
//                     map: [
//                         ['~', './src'],
//                     ],
//                     extensions: ['.js', '.jsx', '.json', '.ts', '.tsx',],
//                 },
//             },
//             'import/extensions': [
//                 '.js',
//                 '.jsx',
//                 '.ts',
//                 '.tsx'
//             ]
//         },
//         plugins: {
//             'react': eslintPluginReact,
//             'react-hooks': eslintPluginReactHooks,
//             'react-refresh': eslintPluginReactRefresh,
//             'simple-import-sort': eslintPluginSimpleImportSort,
//         },
//         rules: {
//             ...js.configs.recommended.rules,
//             ...eslintPluginReact.configs.recommended.rules,
//             ...eslintPluginReact.configs['jsx-runtime'].rules,
//             ...eslintPluginReactHooks.configs.recommended.rules,
//             'react/jsx-no-target-blank': 'off',
//             'react-refresh/only-export-components': ['warn', {
//                 allowConstantExport: true,
//             }],
//             'no-console': 'warn',
//             'no-debugger': 'error',
//             'no-param-reassign': 'error',
//             'react/react-in-jsx-scope': 'off',
//             'simple-import-sort/exports': 'error',
//             'simple-import-sort/imports': ['error', {
//                 'groups': [
//                     ['^react'],
//                     ['^'],
//                     ['^@mui'],
//                     ['^@/', '^\\u0000@/'],
//                 ]
//             }],
//         },
//     },
//     {
//         files: ['vite.config.js'],
//         languageOptions: {
//             globals: globals.node,
//         },
//     },
//     {
//         files: ['**/*.test.{js,jsx}'],
//         languageOptions: {
//             globals: globals.jest,
//         },
//     },
// ]
