package furhatos.app.chatgpt_furhat.flow.main
import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.util.Language


class YesEnum :  EnumEntity(speechRecPhrases = true){
    override fun getEnum(lang: Language): List<String> {
        return listOf("Yes", "Sure", "Of course")
    }
}

class YesIntent(val YesEnum: YesEnum? =null) : Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("@YesEnum")
    }
}

class NoEnum :  EnumEntity(speechRecPhrases = true){
    override fun getEnum(lang: Language): List<String> {
        return listOf("No")
    }
}

class NoIntent(val NoEnum: NoEnum? =null) : Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("@NoEnum")
    }
}

class StopConversationEnum :  EnumEntity(speechRecPhrases = true){
    override fun getEnum(lang: Language): List<String> {
        return listOf(
            "Goodbye", "Bye"
        )
    }
}

class StopConversationIntent(val StopConversationEnum: StopConversationEnum? =null) : Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("@StopConversationEnum")
    }
}