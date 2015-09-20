<?php

	/*
	Each protcol is given:
	- 1st parameter: json decoded array
	- 2nd parameter: tempdata that was stored in database last

	Each protocol must return an array with properties:
	- store: what is stored in database, becomes the symkey on the last call
	- send: what is sent back to the user;
	*/
	
	class Protocol
	{
		public static $test;
	}

	Protocol::$test = array(

		"0" => function($jdata, $tempdata) {
				$hash = hash("sha256", $jdata["secret"]);
				$ret = array();
				$key = substr($hash, 0, 16);
				$ret["store"] = base64_encode($key);
				$ret["send"] = base64_encode($key);
				return $ret;
			}
	);
?>