<?php
	require_once("../lib/dataAccess.class.php");
	$authcode = $_GET["authcode"];
	$res = DataAccess::verify($authcode);
	$ret = array("status" => "");
	if($res->isError()) {
		$ret["status"] = "Failure";
	} else {
		$ret["status"] = "Success";
	}
	echo json_encode($ret);
?>