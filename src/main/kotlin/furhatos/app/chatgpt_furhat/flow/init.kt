package furhatos.app.chatgpt_furhat.flow

import furhatos.app.chatgpt_furhat.flow.main.Idle
import furhatos.app.chatgpt_furhat.flow.main.Greeting
import furhatos.app.chatgpt_furhat.setting.DISTANCE_TO_ENGAGE
import furhatos.app.chatgpt_furhat.setting.MAX_NUMBER_OF_USERS
import furhatos.autobehavior.enableSmileBack
import furhatos.autobehavior.setDefaultMicroexpression
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.users
import furhatos.flow.kotlin.voice.Voice

val Init: State = state {
    init {
        /** Set our default interaction parameters */
        users.setSimpleEngagementPolicy(DISTANCE_TO_ENGAGE, MAX_NUMBER_OF_USERS)
        furhat.setCharacter("Rene")
        furhat.voice = Voice("Matthew")
        furhat.enableSmileBack = true
        furhat.ledStrip.solid(java.awt.Color.WHITE )
        furhat.setDefaultMicroexpression(blinking = true, facialMovements= true, eyeMovements = false)
    }
    onEntry {
        /** start interaction */
        when {
            furhat.isVirtual() -> goto(Greeting) // Convenient to bypass the need for user when running Virtual Furhat
            users.hasAny() -> {
                furhat.attend(users.random)
                goto(Greeting)
            }
            else -> goto(Idle)
        }
    }

}
