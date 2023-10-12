package com.abhi.spring_task_app.service

import com.abhi.spring_task_app.data.Task
import com.abhi.spring_task_app.data.model.TaskCreateRequest
import com.abhi.spring_task_app.data.model.TaskDto
import com.abhi.spring_task_app.data.model.TaskUpdateRequest
import com.abhi.spring_task_app.exception.BadRequestException
import com.abhi.spring_task_app.exception.TaskNotFoundException
import com.abhi.spring_task_app.repository.TaskRepository
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field
import java.util.stream.Collectors
import kotlin.reflect.full.memberProperties

@Service
class TaskService(private val repository: TaskRepository) {

    private fun convertEntityToDto(task: Task) :TaskDto {
        return TaskDto(task.id,
                task.description,
                task.isReminderSet,
                task.isTaskOpen,
                task.createdOn,
                task.priority)
    }

    private fun assignValuesToEntity(task: Task, taskCreateRequest: TaskCreateRequest) {
        task.description = taskCreateRequest.description
        task.isReminderSet = taskCreateRequest.isReminderSet
        task.isTaskOpen = taskCreateRequest.isTaskOpen
        task.createdOn = taskCreateRequest.createdOn
        task.priority = task.priority
    }

    private fun checkFotTaskId(id : Long) {
        if(!repository.existsById(id)) {
            throw TaskNotFoundException("Task with ID: $id does not exist")
        }
       // repository.findTaskById(id)
    }

    fun getAllTasks():List<TaskDto>  =
            repository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList())

    fun getAllOpenTasks(): List<TaskDto> =
            repository.queryAllByTaskOpen().stream().map(this::convertEntityToDto).collect(Collectors.toList())

    fun getAllClosedTasks(): List<TaskDto> =
            repository.queryAllClosedTasks().stream().map(this::convertEntityToDto).collect(Collectors.toList())

    fun getTaskById(id: Long) : TaskDto{
        checkFotTaskId(id)
        val task = repository.findTaskById(id)
        return convertEntityToDto(task)
    }

    fun createTask(createRequest: TaskCreateRequest): TaskDto {
        if(repository.doesDescriptionExist(createRequest.description)) {
            throw BadRequestException("There is already a task with description: ${createRequest.description}")
        }
      val task = Task()
      assignValuesToEntity(task,createRequest)
      val savedTask = repository.save(task)
      return convertEntityToDto(savedTask)
    }

    fun updateTask(id: Long, updateRequest: TaskUpdateRequest): TaskDto {
       checkFotTaskId(id)
       val existingTask = repository.findTaskById(id);

        for (prop in TaskUpdateRequest::class.memberProperties) {
            if (prop.get(updateRequest) != null) {
                val field: Field? = ReflectionUtils.findField(Task::class.java, prop.name)
                field?.let {
                    it.isAccessible = true
                    ReflectionUtils.setField(it, existingTask, prop.get(updateRequest))
                }
            }
        }
        val savedTask = repository.save(existingTask)
        return convertEntityToDto(savedTask)
    }

    fun deleteTask(id: Long):String  {
        checkFotTaskId(id)
        repository.deleteById(id)
        return "Task with id: $id has been deleted."
    }

}