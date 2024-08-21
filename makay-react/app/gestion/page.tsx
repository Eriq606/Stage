'use client'

import { redirect, useRouter } from "next/navigation";

export default function GestionCommande(){
    const router=useRouter()
    const logout= ()=>{
        fetch("http://localhost:8080/logout")
        .then(()=>{
            router.push("/");
        })
    }
    return(
        <>
            <h1>Welcome!</h1>
            <button onClick={logout}>Se déconnecter</button>
        </>
    );
}