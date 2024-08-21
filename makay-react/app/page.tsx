'use client'

import { ReponseREST } from "@/types/reponserest"
import { redirect, useRouter } from "next/navigation"
import { useState, FormEvent } from "react"

export default function Login(){
    const router=useRouter()
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [bouton, setBouton] = useState<string>("Se connecter");
    const [message, setMessage] = useState<string>("");
    async function onSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault()
        setIsLoading(true) // Set loading to true when the request starts
        const formData = new FormData(event.currentTarget)
        setBouton("Connexion...");
        const response = await fetch('http://localhost:8080/login', {
            method: 'POST',
            body: formData,
        })
    
        // Handle response if necessary
        const data : ReponseREST = await response.json()
        if(data.code=="200"){
            router.push("/gestion");
        }else{
            setMessage(data.message);
            console.log(data.message)
        }
        setBouton("Se connecter")
    }
    return(
        <>
            <div className="container-fluid position-relative d-flex p-0">
                <div className="container-fluid">
                    <div className="row h-100 align-items-center justify-content-center" style={{minHeight: "100vh"}}>
                        <div className="col-12 col-sm-8 col-md-6 col-lg-5 col-xl-4">
                            <div className="bg-secondary rounded p-4 p-sm-5 my-4 mx-3">
                                <div className="d-flex align-items-center justify-content-between mb-3">
                                    <a href="index.html" className="">
                                        <h3 className="text-primary"><i className="fa fa-user-edit me-2"></i>Makay</h3>
                                    </a>
                                    <h3>Connexion</h3>
                                </div>
                                <form onSubmit={onSubmit}>
                                    <div className="form-floating mb-3">
                                        <input type="email" className="form-control" name="email" id="floatingInput" placeholder="adresse@example.com"/>
                                        <label htmlFor="floatingInput">Email</label>
                                    </div>
                                    <div className="form-floating mb-3">
                                        <input type="password" className="form-control" name="motdepasse" id="floatingPassword" placeholder=""/>
                                        <label htmlFor="floatingPassword">Mot de passe</label>
                                    </div>
                                    {/* <div className="d-flex align-items-center justify-content-between mb-4">
                                        <div className="form-check">
                                            <input type="checkbox" className="form-check-input" id="exampleCheck1"/>
                                            <label className="form-check-label" htmlFor="exampleCheck1">Check me out</label>
                                        </div>
                                        <a href="">Forgot Password</a>
                                    </div> */}
                                    <button type="submit" className="btn btn-primary py-3 w-100 mb-4">{bouton}</button>
                                    <p style={{color:"red"}}>{message}</p>
                                    {/* <p className="text-center mb-0">Don't have an Account? <a href="">Sign Up</a></p> */}
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}