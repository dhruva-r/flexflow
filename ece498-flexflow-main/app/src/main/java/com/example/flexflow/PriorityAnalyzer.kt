package com.example.flexflow

import java.util.Calendar
import com.example.flexflow.Schedule
//urgency based on due date for tasks within the past 2 weeks
fun getTimeRestaintScore(task: Task, otherTasks: MutableList<Task>): Double{
	val calendar = Calendar.getInstance()
	calendar.setTimeInMillis(System.currentTimeMillis() + 1209600000)
	calendar.set(Calendar.HOUR_OF_DAY, 23)
	calendar.set(Calendar.MINUTE, 59)
	calendar.set(Calendar.SECOND, 59)
	calendar.set(Calendar.MILLISECOND, 999)
	val timeTwoWeeksfromNow: Long = calendar.getTimeInMillis()
	val taskDeadline: Long = task.deadline
	if(taskDeadline > timeTwoWeeksfromNow){
		return 0.0
	}

	var counter: Int = 0
	for(item: Task in otherTasks){
		if(item.deadline > timeTwoWeeksfromNow){
			break
		}

		if(taskDeadline > item.deadline){
			counter++
		}else{
			break
		}
	}
	var score: Double = 1.0 - (counter / (otherTasks.size + 1))
	return score
}

//based against priority of different tasks
fun getImportanceScore(task: Task, otherTasks: MutableList<Task>, timeTwoWeeksfromNow : Double): Double{
	var tempTasks: MutableList<Task> = otherTasks
	tempTasks.sortByDescending({it.priority})
	val taskPriority: Double = task.priority
	var counter: Int = 0
	for(item: Task in tempTasks){
		if(item.deadline > timeTwoWeeksfromNow){
			break
		}

		if(taskPriority > item.priority){
			counter++
		}else{
			break
		}
	}
	var score: Double = 1.0 - (counter / (otherTasks.size + 1))
	return score
}

//return priority from 0 (least important) to 1 (most important)
fun calculatePriority(task: Task, otherTasks: MutableList<Task>): Double{
	var timeRestraintWeight: Double = 1.0
	var importanceWeight: Double = 0.5
	var requisiteWeight: Double = 0.0
	var timeCommitmentWeight: Double = 0.0
	var complexityWeight: Double = 0.0

	var timeRestraintScore: Double = getTimeRestaintScore(task, otherTasks)
	var importanceScore: Double = getImportanceScore(task, otherTasks, 0.0)
	var requisiteScore: Double = 0.0 //gg cope
	var timeCommitmentScore: Double = 0.0 //tensor flow stuff
	var complexityScore: Double = 0.0 //tensor flow stuff


	var priority: Double = (timeRestraintWeight * timeRestraintScore) + 
							(importanceWeight * importanceScore) + 
							(requisiteWeight * requisiteScore) + 
							(timeCommitmentWeight * timeCommitmentScore) +
							(complexityWeight * complexityScore) /
							(timeRestraintWeight + importanceWeight + requisiteWeight + timeCommitmentWeight + complexityWeight)
	return priority
}