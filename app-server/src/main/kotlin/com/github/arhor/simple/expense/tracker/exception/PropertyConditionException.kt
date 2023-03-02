package com.github.arhor.simple.expense.tracker.exception;

abstract class PropertyConditionException(
    private val name: String,
    private val condition: String,
) : RuntimeException("Property [$name] exception occurred, caused by condition [$condition]"), Destructurable {

    override val components: Array<out Any?>
        get() = arrayOf(name, condition)
}

class EntityNotFoundException(name: String, condition: String) : PropertyConditionException(name, condition)
class EntityDuplicateException(name: String, condition: String) : PropertyConditionException(name, condition)
