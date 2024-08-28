# Basic setup of SCM and some refernces

RUN TAILWIND CSS ON EACH PAGE 
1. Run the command -> npx tailwindcss -i ./src/main/resources/static/css/input.css -o ./src/main/resources/static/css/output.css --watch
2. include output.css in index.html
3. if you want flowbite then add css and javascript using cdn in html
<link href="https://cdn.jsdelivr.net/npm/flowbite@2.4.1/dist/flowbite.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/flowbite@2.4.1/dist/flowbite.min.js"></script>





How to add Login with google setup -> 
1. Add OAuth2 client starter dependency
2. Google cloud console 
Create new project
	2.1 go to cloud overview -> API AND Services -> OAuth consent screen -> external
		App domain -> http://localhost:8081
		Add or remove scopes -> email, profile, openid
	2.2 Credentials -> OAuth client id
		URI -> http://localhost:8081
		redirect URI -> http://localhost:8081/login/oauth2/code/google 
		client id = 1049965223705-l4gcgcjov1n9mqaf4a6rf4hhlj3f67hd.apps.googleusercontent.com
		client secret = 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
3. Add the oauth login configuration (by default submit at /oauth2/authorization/google)
4. login Page /login and successHandler
5. in your success handler you are getting data ,  save kra skte hai data based on provider information



How to add login with github
1. Setting -> developer setting -> new Github app
2. Homepage Url -> http://localhost:8081
	Callback url -> http://localhost:8081/login/oauth2/code/github
	Permission -> email = read only and profile = read and write
	client id = Iv23liO82jTbAmkLU7kr
	client secret = f8fd71da9d47381333bda1cfb84316a14f4128ce
3. by default submit at /oauth2/authorization/github (include in login.html)

   

Custom login page reference -> https://stackoverflow.com/questions/78493526/error-405-in-spring-security-6-with-custom-login-page
