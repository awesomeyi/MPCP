<?php
	require_once("../lib/dataAccess.class.php");
	$authcode = $_GET["authcode"];
	$res = DataAccess::logOut($authcode);
	$ret = array("status" => "", "message" => $res->getMessage());
	if($res->isError()) {
		$ret["status"] = "Failure";
		echo json_encode($ret);
	} else {
		$ret["status"] = "Success";
		echo json_encode($ret);
	}
?>