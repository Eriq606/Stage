import { useState, FormEvent } from "react"

export default function Login(){
    const [isLoading, setIsLoading] = useState<boolean>(false)
 
    async function onSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault()
        setIsLoading(true) // Set loading to true when the request starts
    
        try {
            const formData = new FormData(event.currentTarget)
            const response = await fetch('http://localhost:8080/login', {
                method: 'POST',
                body: formData,
            })
        
            // Handle response if necessary
            const data = await response.json()
            // ...
        } catch (error) {
            console.error(error)
        } finally {
            setIsLoading(false) // Set loading to false when the request completes
        }
    }
    return(
        <>
            <div className="container-fluid position-relative d-flex p-0">
                <div id="spinner" className="show bg-dark position-fixed translate-middle w-100 vh-100 top-50 start-50 d-flex align-items-center justify-content-center">
                    <div className="spinner-border text-primary" style={{width: "3rem", height: "3rem"}} role="status">
                        <span className="sr-only">Loading...</span>
                    </div>
                </div>
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
                                        <input type="email" className="form-control" id="floatingInput" placeholder="name@example.com"/>
                                        <label htmlFor="floatingInput">Email</label>
                                    </div>
                                    <div className="form-floating mb-4">
                                        <input type="password" className="form-control" id="floatingPassword" placeholder="Password"/>
                                        <label htmlFor="floatingPassword">Mot de passe</label>
                                    </div>
                                    {/* <div className="d-flex align-items-center justify-content-between mb-4">
                                        <div className="form-check">
                                            <input type="checkbox" className="form-check-input" id="exampleCheck1"/>
                                            <label className="form-check-label" htmlFor="exampleCheck1">Check me out</label>
                                        </div>
                                        <a href="">Forgot Password</a>
                                    </div> */}
                                    <button type="submit" className="btn btn-primary py-3 w-100 mb-4">Se connecter</button>
                                    {/* <p className="text-center mb-0">Don't have an Account? <a href="">Sign Up</a></p> */}
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
            <script src="lib/chart/chart.min.js"></script>
            <script src="lib/easing/easing.min.js"></script>
            <script src="lib/waypoints/waypoints.min.js"></script>
            <script src="lib/owlcarousel/owl.carousel.min.js"></script>
            <script src="lib/tempusdominus/js/moment.min.js"></script>
            <script src="lib/tempusdominus/js/moment-timezone.min.js"></script>
            <script src="lib/tempusdominus/js/tempusdominus-bootstrap-4.min.js"></script>
            <script src="js/main.js"></script>
        </>
    );
}