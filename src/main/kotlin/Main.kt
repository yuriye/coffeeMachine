package machine

import kotlin.system.exitProcess

class CoffeeMachine(var water:Int, var milk: Int, var beans: Int, var disposableCups: Int, var money: Int, var recipes: List<Recipe>) {

    enum class WaitingState {
        WAITING_FOR_COMMAND,
        WAITING_FOR_CHOICE,
        WAITING_FOR_WATER_VOLUME,
        WAITING_FOR_MILK_VOLUME,
        WAITING_FOR_BEANS_QUANTITY,
        WAITING_FOR_CUPS_QUANTITY
    }

    val actionMessage = "Write action (buy, fill, take, remaining, exit): "
    private val choiceMessage = "What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: "
    private val waterMessage = "Write how many ml of water do you want to add: "
    private val milkMessage = "Write how many ml of milk do you want to add: "
    private val beansMessage = "Write how many grams of coffee beans do you want to add: "
    private val cupsMessage = "Write how many disposable cups of coffee do you want to add: "

    private val state: String
        get() = "\nThe coffee machine has:\n$water ml of water\n$milk ml of milk\n$beans g of coffee beans\n$disposableCups disposable cups\n\$$money of money\n"

    private var waitingState = WaitingState.WAITING_FOR_COMMAND

    fun request(input: String): String {

        when (waitingState) {

            WaitingState.WAITING_FOR_COMMAND -> {
                when (input) {

                    "exit" -> exitProcess(0)

                    "remaining" -> return "$state\n$actionMessage"

                    "buy" -> {
                        waitingState = WaitingState.WAITING_FOR_CHOICE
                        return "\n$choiceMessage"
                    }

                    "fill" -> {
                        waitingState = WaitingState.WAITING_FOR_WATER_VOLUME
                        return "\n$waterMessage"
                    }

                    "take" -> {
                        val tmp = this.money
                        this.money = 0
                        return "I gave you \$$tmp\n\n$actionMessage"
                    }

                }
            }

            WaitingState.WAITING_FOR_CHOICE -> {

                waitingState = WaitingState.WAITING_FOR_COMMAND

                when (input) {

                    "back" -> return "\n$actionMessage\n"

                    "1", "2", "3" -> return "${prepare(recipes[input.toInt() - 1])}\n$actionMessage"
                }
            }

            WaitingState.WAITING_FOR_WATER_VOLUME -> {
                this.water += input.toInt()
                waitingState = WaitingState.WAITING_FOR_MILK_VOLUME
                return milkMessage
            }

            WaitingState.WAITING_FOR_MILK_VOLUME -> {
                this.milk += input.toInt()
                waitingState = WaitingState.WAITING_FOR_BEANS_QUANTITY
                return beansMessage
            }

            WaitingState.WAITING_FOR_BEANS_QUANTITY -> {
                this.beans += input.toInt()
                waitingState = WaitingState.WAITING_FOR_CUPS_QUANTITY
                return cupsMessage
            }

            WaitingState.WAITING_FOR_CUPS_QUANTITY -> {
                this.disposableCups += input.toInt()
                waitingState = WaitingState.WAITING_FOR_COMMAND
                return "\n$actionMessage"
            }
        }

        return ""
    }

    fun prepare(recipe: Recipe, cups: Int = 1): String {
        when {
            recipe.water > this.water -> return "Sorry, not enough water!\n"
            recipe.milk > this.milk -> return "Sorry, not enough milk!\n"
            recipe.beans > this.beans -> return "Sorry, not enough coffee beans!\n"
            cups > this.disposableCups ->return "Sorry, not enough disposable cups!\n"
        }

        this.water -= recipe.water * cups
        this.milk -= recipe.milk * cups
        this.beans -= recipe.beans * cups
        this.disposableCups -= cups
        this.money += recipe.price * cups

        return "I have enough resources, making you a coffee!\n"
    }
}

data class Recipe(val water:Int, val milk: Int, val beans: Int, val price: Int)

fun run() {
    val recipes = listOf(
        Recipe(250, 0, 16, 4),
        Recipe(350, 75, 20, 7),
        Recipe(200, 100, 12, 6)
    )

    val coffeeMachine = CoffeeMachine(400, 540, 120, 9, 550, recipes)

    print(coffeeMachine.actionMessage)

    while (true)
        print(coffeeMachine.request(readLine()!!))
 }

fun main() {
    run()
}