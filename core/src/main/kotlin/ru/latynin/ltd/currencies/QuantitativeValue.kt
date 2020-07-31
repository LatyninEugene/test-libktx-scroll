package ru.latynin.ltd.currencies

abstract class QuantitativeValue{

    var value: Int = 0
        protected set

    protected val listeners: MutableList<(Int, Int)->Unit> = mutableListOf()

    fun add(value: Int): Boolean{
        if(value == 0) return true
        val oldValue = this.value
        this.value += value
        if(this.value < 0){
            this.value -= value
            return false
        }
        listeners.forEach { it(this.value, oldValue) }
        return true
    }

    fun set(value: Int): Boolean{
        this.value = 0
        return add(value)
    }

    fun minus(value: Int){
        add(-value)
    }

    fun addListener(func: (Int, Int)->Unit){
        listeners.add(func)
    }

}