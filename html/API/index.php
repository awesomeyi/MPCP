<?php
	require_once("../lib/dataAccess.class.php");
	$request = $_GET['request'];

	function getRet($res) {
		$ret = array("status" => "", "message" => $res->getMessage());
		if($res->isError()) {
			$ret["status"] = "Failure";
		} else {
			$ret["status"] = "Success";
		}
		return $ret;
	}

	$requestChoice = array(

			"login" => function() {
				$uname = $_POST["username"];
				$pword = $_POST["password"];
				$res = DataAccess::logIn($uname, $pword);
				$ret = getRet($res);
				if(!$res->isError()) {
					$ret["message"] = "Log in success!";
					$ret["authcode"] = $res->getMessage();
				}
				return $ret;
			},

			"register" => function() {
				$uname = $_POST["username"];
				$pword = $_POST["password"];
				$res = DataAccess::registerUser($uname, $pword);
				return getRet($res);
			},

			"logout" => function() {
				$authcode = $_GET["authcode"];
				$res = DataAccess::logOut($authcode);
				return getRet($res);
			},

			"verify" => function() {
				$authcode = $_GET["authcode"];
				$res = DataAccess::verify($authcode);
				return getRet($res);
			}

	);
	if(!isset($requestChoice[$request])) {
		header("HTTP/1.0 404 Not Found");
		die();
	}
	$ret = call_user_func($requestChoice[$request]);
	echo json_encode($ret);
?>