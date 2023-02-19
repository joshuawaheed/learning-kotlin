package com.joshuawaheed.a7minuteworkout

class ExerciseModel(
    private var id: Int,
    private var name: String,
    private var image: Int,
    private var isCompleted: Boolean,
    private var isSelected: Boolean
) {
    fun getId(): Int {
        return id
    }

    fun getImage(): Int {
        return image
    }

    fun getIsCompleted(): Boolean {
        return isCompleted
    }

    fun getIsSelected(): Boolean {
        return isSelected
    }

    fun getName(): String {
        return name
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun setImage(image: Int) {
        this.image = image
    }

    fun setIsCompleted(isCompleted: Boolean) {
        this.isCompleted = isCompleted
    }

    fun setIsSelected(isSelected: Boolean) {
        this.isSelected = isSelected
    }

    fun setName(name: String) {
        this.name = name
    }
}