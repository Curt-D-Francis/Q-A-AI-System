import { useState, useRef } from "react"

function Upload() {
    const [Pdf, SetPdf] = useState<File | null>(null)
    const fileInput = useRef<HTMLInputElement>(null)
    async function handlePDFUpload(){
        if (Pdf != null){
            var PDF_Payload = new FormData()
            PDF_Payload.append("Uploaded_PDF_File", Pdf)

            const response = await fetch("http://localhost:8080/api/document/upload",{
                method: "POST",
                body: PDF_Payload,
            })

            if (!response.ok){
                throw new Error(`HTTP Error. Stats: ${response.status}`)
            }
        }
    }
    function handleFileChange(event:React.ChangeEvent<HTMLInputElement>){
            var newPdf = (event.target as HTMLInputElement)?.files?.[0]
            SetPdf(newPdf as File)
    }

    function handleOnDrop(event: React.DragEvent<HTMLDivElement>){
        event.preventDefault()
        var newPdf = event.dataTransfer?.files?.[0]
        if (newPdf.type != "application/pdf") return
        SetPdf(newPdf)
    }
    return (
        <div className="Upload">
            <div className="currentFile">
                {Pdf ? <p>{Pdf.name}</p> : <p/>}
            </div>
            
            <div className="fileUpload"
            onDrop={handleOnDrop}
            onDragOver={(event) => event.preventDefault()}>
                <input className="hiddenFileInput"
                accept="application/pdf"
                ref={fileInput}
                type="file"
                onChange={handleFileChange}
                
                 />

                <button
                onClick={handlePDFUpload}
                className="pdfUploadButton">
                    Upload 
                </button>
            </div>
           
        </div>
    )
}

export default Upload