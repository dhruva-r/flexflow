package com.example.flexflow


import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

//how to import things?

data class Task(
	var name: String,
	var details: String,
	var priority: Double, //0 to 1 with 1 being highest priority
	var deadline: Long, //unix milliseconds
	var is_completed: Boolean,
	
	//var prerequisite: MutableList<task>, //tasks that are prerequisites for this task
	//var dependancy: MutableList<task> //tasks that depend on this task
)


//TODO task list be an avl tree?

class Schedule{
	var isUpdated: Boolean  = false//not sure how to use this rn
	var myTasks: MutableList<Task> = mutableListOf<Task>()

	//gets the task of the day given the date
	//also gets tasks in the past
	fun getTasksForDate(date: Date): MutableList<Task>{
		var tempDate: Date = date
		val calendar = Calendar.getInstance()
		calendar.time = tempDate
		calendar.set(Calendar.HOUR_OF_DAY, 23)
		calendar.set(Calendar.MINUTE, 59)
		calendar.set(Calendar.SECOND, 59)
		calendar.set(Calendar.MILLISECOND, 999)
		val timeMilliseconds: Long = calendar.getTimeInMillis()
	
		var tasks: MutableList<Task> = myTasks.filter({it.deadline <= timeMilliseconds}).toMutableList()
		for(item: Task in tasks){
			if(item.is_completed == false){
				tasks.add(item)
			}
		}
		return tasks
	}

	//get the tasks for the current day
	//also gets tasks in the past
	fun getDailyTasks(): MutableList<Task>{
		//if its 11pm local time consider also retreiving the next days tasks?
	
		val calendar = Calendar.getInstance()
		calendar.time = Date()
		calendar.setTimeInMillis(System.currentTimeMillis())
		val date: Date = calendar.getTime()
		
		var tasks: MutableList<Task> = getTasksForDate(date)
		return tasks
	}

	//get the tasks for the current week
	//also gets tasks in the past
	//week starts on mondays for everywhere except US, CANADA and JAPAN according to ISO 8601
	//assume week starts on monday
	fun getWeeklyTasks(): MutableList<Task>{
		val calendar = Calendar.getInstance()
		calendar.time = Date()
		calendar.setTimeInMillis(System.currentTimeMillis())
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
		val date: Date = calendar.getTime()
		
		var tasks: MutableList<Task> = getTasksForDate(date)
		return tasks
	}

	//get the tasks for the current month
	//also gets tasks in the past
	fun getMonthlyTasks(): MutableList<Task>{
		val calendar = Calendar.getInstance()
		calendar.time = Date()
		calendar.setTimeInMillis(System.currentTimeMillis())
		//TODO: something something time manipulation

		val date: Date = calendar.getTime()
		
		var tasks: MutableList<Task> = getTasksForDate(date)
		return tasks
	}

	//get prerequisites and dependancies?
	fun addTask(name: String, details: String, deadline: Long){
		val calendar = Calendar.getInstance()
		calendar.time = Date(deadline)
		val deadlineMilliseconds: Long = calendar.getTimeInMillis()

		val task = Task(name, details, 0.0, deadlineMilliseconds, false)
		val priority: Double = calculatePriority(task, myTasks)

		val task2 = Task(name, details, priority, deadlineMilliseconds, false)
		myTasks.add(task2)
		myTasks.sortBy({it.deadline})
	}
}