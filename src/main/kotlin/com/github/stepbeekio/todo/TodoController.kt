package com.github.stepbeekio.todo

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException

@RestController
@RequestMapping("/todo")
class TodoController(
    private val todoRepository: TodoRepository,
) {

    @GetMapping
    fun index(@PageableDefault(20) pageable: Pageable): Page<TodoResponse> =
        todoRepository.findAll(pageable).map { TodoResponse.from(it) }

    @PostMapping
    fun create(@RequestBody request: CreateTodoRequest): TodoResponse =
        todoRepository.save(request.create()).let { TodoResponse.from(it) }

    @PostMapping("/random")
    fun random(): TodoResponse =
        todoRepository.save(Todo.random()).let { TodoResponse.from(it) }

    @PutMapping("/{id}")
    fun edit(@PathVariable("id") id: Long, @RequestBody request: EditTodoRequest): TodoResponse =
        todoRepository.findByIdOrNull(id)?.let {
            request.edit(it)
            val result = todoRepository.save(it)

            TodoResponse.from(result)
        } ?: throw HttpClientErrorException(HttpStatus.NOT_FOUND)

}
