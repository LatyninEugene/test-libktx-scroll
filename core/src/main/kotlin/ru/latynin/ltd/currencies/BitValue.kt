package ru.latynin.ltd.currencies

abstract class BitValue {

    abstract val types: List<String>

    var value: Float = 0f
        protected set
    var currentType: Int = 0
        protected set

    protected val listeners: MutableList<(Float, Int, Float, Int)->Unit> = mutableListOf()

    fun add(value: Float, type: Int = 0): Boolean{
        if(value == 0f) return true
        val oldValue = this.value
        val oldType = this.currentType
        this.value += changeValue(value, type)
        if(this.value < 0){
            this.value -= changeValue(value, type)
            return false
        }
        updateType()
        listeners.forEach { it(this.value, currentType, oldValue, oldType) }
        return true
    }

    fun set(value: Float, type: Int = 0): Boolean {
        this.value = 0f
        this.currentType = 0
        return add(value, type)
    }

    fun minus(value: Float, type: Int = 0){
        add(-value, type)
    }

    private fun changeValue(value: Float, type: Int): Float{
        var new = value
        if (type < currentType){
            for (i in 1..currentType-type){
                new /= 1024
            }
        } else if (type > currentType) {
            for (i in 1..type-currentType){
                new *= 1024
            }
        }
        return new
    }

    private fun updateType(){
        // check increment
        while(currentType < types.size && this.value >= 1024){
            this.value /= 1024
            currentType++
        }
        // check decrement
        while(currentType != 0 && this.value < 1){
            this.value *= 1024
            currentType--
        }
    }

    fun addListener(func: (Float, Int, Float, Int)->Unit){
        listeners.add(func)
    }


}