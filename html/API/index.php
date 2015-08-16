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
	function authAction($fun) {
		$fun = 'DataAccess::'.$fun;
		$authcode = $_GET["authcode"];
		$res = call_user_func($fun, $authcode);
		return getRet($res);
	}
	function notFound() {
		header("HTTP/1.0 404 Not Found");
		die();
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
				return authAction("logOut");
			},

			"verify" => function() {
				return authAction("verify");
			},

			"username" => function() {
				return authAction("getUsername");
			},

			"bank" => array(

				"accounts" => function() {
					return authAction("getAccounts");
				},

				"transfer" => array(

					"create" => function() {
						$authcode = $_GET["authcode"];
						$accountid = $_POST["accountid"];
						$destUsername = $_POST["destUsername"];
						$amount = $_POST["amount"];
						$res = DataAccess::requestTransfer($authcode, $accountid, $destUsername, $amount);
						return getRet($res);
					},

					"cancel" => function() {
						$authcode = $_GET["authcode"];
						$tid = $_POST["transferid"];
						$res = DataAccess::cancelTransfer($authcode, $tid);
						return getRet($res);
					},

					"accept" => function() {
						$authcode = $_GET["authcode"];
						$tid = $_POST["transferid"];
						$res = DataAccess::acceptTransfer($authcode, $tid);
						return getRet($res);
					}

				),

				"transfers" => function() {
					return authAction("getTransfers");
				}
			)

	);

	$raction = explode("/", $request);
	$cd = $requestChoice;
	for($i = 0; $i < count($raction) - 1; ++$i) {
		$cur = $cd[$raction[$i]];
		if(isset($cur) && is_array($cur)) {
			$cd = $cur;
		} else {
			notFound();
		}
	}
	$last = $raction[count($raction) - 1];

	if(!is_callable($cd[$last])) {
		notFound();
	}
	$ret = call_user_func($cd[$last]);
	echo json_encode($ret);
?>