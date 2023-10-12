package com.abhi.spring_task_app.controller

import com.abhi.spring_task_app.data.model.TaskCreateRequest
import com.abhi.spring_task_app.data.model.TaskDto
import com.abhi.spring_task_app.data.model.TaskUpdateRequest
import com.abhi.spring_task_app.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/api/")
class TaskController(val service: TaskService) {
    @GetMapping("all-tasks")
    fun getAllTasks() : ResponseEntity<List<TaskDto>> = ResponseEntity(service.getAllTasks(), HttpStatus.OK)

    @GetMapping("open-tasks")
    fun getAllOpenTasks() : ResponseEntity<List<TaskDto>> = ResponseEntity(service.getAllOpenTasks(), HttpStatus.OK)

    @GetMapping("closed-tasks")
    fun getAllCloseTasks() : ResponseEntity<List<TaskDto>> = ResponseEntity(service.getAllClosedTasks(), HttpStatus.OK)

    @GetMapping("task/{id}")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<TaskDto> =
            ResponseEntity(service.getTaskById(id), HttpStatus.OK)
    @PostMapping("create")
    fun createTask(@Valid @RequestBody createRequest: TaskCreateRequest): ResponseEntity<TaskDto> =
            ResponseEntity(service.createTask(createRequest), HttpStatus.OK)
    @PatchMapping("update/{id}")
    fun updateTask(
            @PathVariable id: Long,
            @Valid @RequestBody updateRequest: TaskUpdateRequest
    ): ResponseEntity<TaskDto> = ResponseEntity(service.updateTask(id, updateRequest), HttpStatus.OK)

    @DeleteMapping("delete/{id}")
    fun deleteTask(@PathVariable id: Long): ResponseEntity<String> =
            ResponseEntity(service.deleteTask(id), HttpStatus.OK)
}