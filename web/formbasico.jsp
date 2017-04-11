<!doctype html>
<html class="no-js" lang="en" dir="ltr">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Foundation for Sites</title>
    <link rel="stylesheet" href="css/foundation.css">
    <link rel="stylesheet" href="css/foundation-icons/foundation-icons.css" />
    <link rel="stylesheet" href="css/app.css">
  </head>
  <body>
<div class="row align-center">
	<div class="column medium-6">
	<h1>Formulario Basico</h1>
		<form method="get" action="${pageContext.request.contextPath}/WebController">
			<div class="input-group">
				<span class="input-group-label"><i class="fi-page-edit size-16"></i></span> 
				<input class="input-group-field" name="email" type="text" value="dromedicas">				
			</div>
			<div class="input-group">
				<span class="input-group-label"><i class="fi-social-twitter size-16"></i></span>
				<input class="input-group-field" name="nombre" type="text" value="@dromedicas">				
			</div>
			<div class="row">
				<div class="column">
					<button class="button float-right" >Guardar</button>
				</div>
			</div>
		</form>
	</div>
</div>
    <script src="js/vendor/jquery.js"></script>
    <script src="js/vendor/what-input.js"></script>
    <script src="js/vendor/foundation.js"></script>
    <script src="js/app.js"></script>
  </body>
</html>
