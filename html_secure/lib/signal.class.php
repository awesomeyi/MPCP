<?php
	#Signal class
	#0 - Error
	#1 - Success

	class ISignal {
		private $mes;
		private $type;

		public function __construct($mes, $type) {
			$this->mes = $mes;
			$this->type = $type;
		}
		public function isError() {
			if($this->type == 0) return True;
			return False;
		}
		public function getMessage() {
			return $this->mes;
		}
	}
	class Signal {
		public static $error;
		public static $dbConnectionError;
		public static $usernameTakenError;
		public static $credentialError;
		public static $authenticationError;
		public static $success;
	}
	Signal::$error = new ISignal("Generic error", 0);
	Signal::$dbConnectionError = new ISignal("Database connection error", 0);
	Signal::$usernameTakenError = new ISignal("This username has been taken", 0);
	Signal::$credentialError = new ISignal("Invalid credentials", 0);
	Signal::$authenticationError = new ISignal("Authentication error", 0);
	Signal::$success = new ISignal("Generic success", 1);
?>