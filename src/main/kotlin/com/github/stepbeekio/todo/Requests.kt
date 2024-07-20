package com.github.stepbeekio.todo

import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


data class TodoResponse(
    val identifier: Long,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val status: TodoStatus,
) {
    companion object {
        private val dataTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

        fun from(todo: Todo): TodoResponse = TodoResponse(
            identifier = todo.identifier,
            content = todo.content,
            createdAt = dataTimeFormatter.format(todo.createdAt.atOffset(ZoneOffset.UTC)),
            updatedAt = dataTimeFormatter.format(todo.updatedAt.atOffset(ZoneOffset.UTC)),
            status = todo.status
        )
    }
}

data class CreateTodoRequest(val content: String) {
    fun create(): Todo =
        Todo.new(content)
}

data class EditTodoRequest(val content: String? = null, val status: TodoStatus? = null) {
    fun edit(todo: Todo) {
        content?.also { todo.content = it }
        status?.also { todo.status = it }
    }
}
