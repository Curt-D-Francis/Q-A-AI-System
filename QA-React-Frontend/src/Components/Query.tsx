import { useState } from "react"



function Query(){
    const [Messages, SetMessage] = useState<Array<{role: string, content: string}>>([])
    const [input, SetInput] = useState<string>("")
    const [loading, SetLoading] = useState<boolean>(false)
    async function sendHandler(){
        if (input !== "" && loading !== true){
            SetLoading(true)
            const savedInput = input
            SetInput("")
            
            const response = await fetch("http://localhost:8080/api/document/query", {
                method: "POST",
                headers:{
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(savedInput),
            })

            if (!response.ok){
                SetLoading(false)
                throw new Error(`HTTP Error. Stats: ${response.status}`)
            }
            const answer = await response.text()
            SetMessage(Messages =>[...Messages, 
                                {role: "User", content: savedInput},
                                {role: "assistant", content: answer}])
            SetLoading(false)
        }
    }
  
  return (
    <div>
        {Messages.map((message, index) => (
        <div key={index} className={message.role === "User" ? "userMessage" : "assistantMessage"}>
            {message.content}
        </div>
        ))}
        <div>
            <input
            type="text"
            value={input}
            onChange={(e) => SetInput(e.target.value)}/>
            
            <button
                onClick={sendHandler}
                className="QueryButton">
                     {loading ? "Loading..." : "Send"}
            </button>
            
        </div>
    </div>
  )

}



export default Query