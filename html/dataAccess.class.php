<?php
	require_once("secret.class.php");
	class DataAccess
	{
		private static $ip = "localhost";
		public function getConnection() {
			return new mysqli(DataAccess::$ip, Secret::$username, Secret::$password, "mpcp");
		}
	}
?>