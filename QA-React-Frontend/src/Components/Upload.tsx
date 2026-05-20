import { useState, useRef } from "react"

function Upload() {
    const [Pdf, SetPdf] = useState<File | null>(null)
    const fileInput = useRef<HTMLInputElement>(null)
    function handlePDFUpload(){
        fileInput.current?.click()
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
                </button>
            </div>
           
        </div>
    )
}

export default Upload