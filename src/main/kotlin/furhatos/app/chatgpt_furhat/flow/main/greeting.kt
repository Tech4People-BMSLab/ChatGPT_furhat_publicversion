package furhatos.app.chatgpt_furhat.flow.main

import furhatos.app.chatgpt_furhat.flow.Parent
import furhatos.autobehavior.enableSmileBack
import furhatos.autobehavior.setDefaultMicroexpression
import furhatos.flow.kotlin.*
import furhatos.gestures.BasicParams
import furhatos.gestures.Gestures
import furhatos.gestures.defineGesture
import furhatos.util.CommonUtils
import khttp.post
import org.json.JSONArray
import org.json.JSONObject


var messages  = JSONArray()
val logger = CommonUtils.getLogger("ChatGPT_furhat")
var stop = 0

val Greeting : State = state(Parent) {
    onEntry {
        furhat.setDefaultMicroexpression(blinking = true, facialMovements= true, eyeMovements = false)
        furhat.enableSmileBack = true
        furhat.ledStrip.solid(java.awt.Color.WHITE)
        furhat.say("Hi I'm Furhat, a social robot, probably the best that you'll meet today!")
        furhat.gesture(Gestures.Wink, async = false)
        furhat.say("I will try to answer with concise answers, you can say ${furhat.voice.emphasis("Tell me more")} to get more information")
        goto(Chat)
    }
}



val Chat: State =state{
    onEntry {
        furhat.setDefaultMicroexpression(blinking = true, facialMovements= true, eyeMovements = false)
        furhat.enableSmileBack = true
        furhat.ledStrip.solid(java.awt.Color.BLUE)
        furhat.ask("What do you want to talk about?")
        furhat.gesture(Gestures.BigSmile ,async = false)
        furhat.listen(8000, endSil = 3000)
        delay(200)
    }
    onReentry {
        stop = 0
        furhat.setDefaultMicroexpression(blinking = true, facialMovements= true, eyeMovements = false)
        furhat.enableSmileBack = true
        furhat.ledStrip.solid(java.awt.Color.BLUE)
        furhat.listen(8000, endSil = 3000)
        furhat.gesture(Gestures.Thoughtful ,async = false)
        delay(200)
    }
    onResponse<StopConversationIntent> {
        while (messages.length() > 0) {
            messages.remove(0)
        }
        furhat.gesture(Gestures.Oh , async = false)
        furhat.say("Oh, I'll miss you!")
        furhat.say("Have great day!")
        furhat.gesture(Gestures.BigSmile, async = false)
        val CloseEyes = defineGesture("CloseEyes") {
            frame(0.4, persist = true) {
                BasicParams.BLINK_RIGHT to 1.0
                BasicParams.BLINK_LEFT to 1.0
            }
        }
        furhat.gesture(CloseEyes, priority=10)
    }
    onNoResponse {
        reentry()
    }
    onResponse<NoIntent>{
        furhat.say("If you don't want to ask me something else you can say Goodbye to stop the conversation")
        reentry()
    }
    onResponse{
        val apiKey = "PRIVATE KEY FOR OPENAI API"
        var prompt = it.text
        val m = JSONObject()
        if(prompt == "Yes" && stop == 1){
            prompt = "Continue"
        }
        stop=0
        m.put("role","user")
        m.put("content",prompt)
        messages.put(m)

        val chatgpt = "https://api.openai.com/v1/chat/completions"


        val res = post(
            url = chatgpt,
            headers = mapOf("Authorization" to "Bearer $apiKey"),
            json = mapOf("model" to "gpt-3.5-turbo" , "messages" to messages, "max_tokens" to 100, "temperature" to 0.7)
        ).jsonObject
        println(res)

        var content = res.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
        var len = res.getJSONObject("usage").get("completion_tokens").toString()
        if( len.toInt() >= 100){
            println("QUI")
            // if the response is longer than 80 tokens, find the last period in the text and cut the string
            val text = content
            val lastPeriodIndex = text.lastIndexOf(".")
            if (lastPeriodIndex > 0) {
                val truncatedText = text.substring(0, lastPeriodIndex + 1)
                content = truncatedText
                stop = 1

                val ans = JSONObject()
                ans.put("role","system")
                ans.put("content",content)
                messages.put(ans)
                furhat.say(content.toString())
                furhat.say(" Would you like me to continue?")
                reentry()
            }
        }

        val ans = JSONObject()
        ans.put("role","system")
        ans.put("content",content)
        messages.put(ans)
        furhat.say(content.toString())
        reentry()
    }
}
