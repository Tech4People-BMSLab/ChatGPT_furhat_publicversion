package furhatos.app.chatgpt_furhat

import furhatos.app.chatgpt_furhat.flow.Init
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill

class Chatgpt_furhatSkill : Skill() {
    override fun start() {
        Flow().run(Init)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
