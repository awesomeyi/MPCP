<?php
	require_once("../lib/dataAccess.class.php");
	$uname = $_POST["username"];
	$pword = $_POST["password"];
	$res = DataAccess::logIn($uname, $pword);
	$ret = array("status" => "", "message" => $res->getMessage());
	if($res->isError()) {
		$ret["status"] = "Failure";
		echo json_encode($ret);
	} else {
		$ret["status"] = "Success";
		$ret["message"] = "Log in success!";
		$ret["authcode"] = $res->getMessage();
		echo json_encode($ret);
	}
?>