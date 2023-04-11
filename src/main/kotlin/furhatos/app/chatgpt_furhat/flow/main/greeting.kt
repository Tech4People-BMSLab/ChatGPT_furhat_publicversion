package furhatos.app.chatgpt_furhat.flow.main

import furhatos.app.chatgpt_furhat.flow.Parent
import furhatos.autobehavior.enableSmileBack
import furhatos.autobehavior.setDefaultMicroexpression
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import furhatos.skills.emotions.UserGestures
import furhatos.util.CommonUtils
import khttp.post
import org.json.JSONArray
import org.json.JSONObject


var messages  = JSONArray()

val Greeting : State = state(Parent) {
    onEntry {
        furhat.setDefaultMicroexpression(blinking = true, facialMovements= true, eyeMovements = false)
        furhat.enableSmileBack = true
        furhat.ledStrip.solid(java.awt.Color.WHITE)
        furhat.say("Hi I'm Furhat, a social robot, probably the best that you'll meet today!")
        furhat.gesture(Gestures.Wink, async = false)
        furhat.ask("Do you want to talk with me?")
        furhat.gesture(Gestures.BigSmile, async = false)
        furhat.listen(endSil = 1000, timeout = 8000, maxSpeech = 30000)
        delay(100)

    }
    onResponse<NoIntent> {
        furhat.ledStrip.solid(java.awt.Color.RED)
        furhat.gesture(Gestures.Oh , async = false)
        furhat.say("Oh, I'll miss you!")
        furhat.gesture(Gestures.ExpressSad, async = false)
        furhat.say("Have great day!")
        furhat.gesture(Gestures.BigSmile, async = false)
    }
    onResponse<YesIntent>{
        furhat.ledStrip.solid(java.awt.Color.GREEN)
        furhat.say("Great! ")
        furhat.gesture(Gestures.BigSmile, async = false)
        furhat.say("Remember to say ${furhat.voice.emphasis("goodbye")} when you want to stop the conversation")
        delay(200)
        goto(Chat)
    }

}

val Chat: State =state{
    onEntry {
        furhat.setDefaultMicroexpression(blinking = true, facialMovements= true, eyeMovements = false)
        furhat.enableSmileBack = true
        furhat.ledStrip.solid(java.awt.Color.BLUE)
        furhat.ask("So, what do you want to talk about?")
        furhat.gesture(Gestures.Thoughtful ,async = false)
        furhat.listen(8000, endSil = 2200)
        delay(200)
    }
    onReentry {
        furhat.setDefaultMicroexpression(blinking = true, facialMovements= true, eyeMovements = false)
        furhat.enableSmileBack = true
        furhat.ledStrip.solid(java.awt.Color.BLUE)
        furhat.gesture(Gestures.Thoughtful ,async = false)
        furhat.listen(8000, endSil = 2200)
        delay(200)
    }

    onResponse<StopConversationIntent> {
        while (messages.length() > 0) {
            messages.remove(0)
        }
        furhat.gesture(Gestures.Oh , async = false)
        furhat.say("Oh, I'll miss you!")
        furhat.gesture(Gestures.ExpressSad, async = false)
        furhat.say("Have great day!")
        furhat.gesture(Gestures.BigSmile, async = false)
    }
    onResponse{
        val apiKey = "-------YOU HAVE TO PUT YOUR OpenAI KEY HERE------"
        val prompt = it.text
        val m = JSONObject()
        m.put("role","user")
        m.put("content",prompt)
        messages.put(m)

        val chatgpt = "https://api.openai.com/v1/chat/completions"


        val res = post(
            url = chatgpt,
            headers = mapOf("Authorization" to "Bearer $apiKey"),
            json = mapOf("model" to "gpt-3.5-turbo" , "messages" to messages, "max_tokens" to 50, "temperature" to 0.7)
        ).jsonObject
        println(res)
        val content = res.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")

        val ans = JSONObject()
        ans.put("role","system")
        ans.put("content",content)
        messages.put(ans)

        furhat.say(content.toString())
        reentry()
    }

}
