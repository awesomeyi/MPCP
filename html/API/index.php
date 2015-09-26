<?php

	require_once("../lib/sessionControl.class.php");

	function notFound() {
		header("HTTP/1.0 404 Not Found");
		die();
	}

	$request = $_GET['request'];
	$requestChoice = array(

		"kap" => function() {
			$json = json_decode(file_get_contents('php://input'), true);
			$step = $json['step'];
			$data = $json['data'];

			$error = array("step" => $step, "data" => "Error");
			$success = array("step" => $step);

			if(!isset($_GET['sessionid'])) {

				//Initialize handshake
				if($step == 1) {
					$ssid = SessionControl::initHandshake($data);
					if(!$ssid) {
						return $error;
					}

					$success["data"] = $ssid;
					return $success;
				}
				return $error;
			}

			$sessionid = $_GET['sessionid'];

			$res = SessionControl::execute($sessionid, $step, $data);
			if(!$res) {
				return $error;
			}
			$success["data"] = $res;
			return $success;
		},

		"session" => function() {
			$success = array("status" => "success");
			$failure = array("status" => "failure");

			if(!isset($_GET['sessionid'])) {
				$failure["data"] = "Error: missing sessionid";
				return $failure;
			}
			$sessionid = $_GET['sessionid'];

			$encrypted = json_decode(file_get_contents('php://input'), true);
			$data = SessionControl::decryptJSON($sessionid, $encrypted["data"]);

			if(!$data) {
				$failure["data"] = "Session error";
				return $failure;
			}
			
			$authcode = $data["authcode"];
			$action = $data["action"];
			$parameters = $data["parameters"];

			//GET request
			$url = "https://".$_SERVER["SERVER_NAME"]."/API/$action?authcode=$authcode";
			$curl = curl_init();

			curl_setopt_array($curl, array(
				CURLOPT_RETURNTRANSFER => 1,
				CURLOPT_URL => $url,
				CURLOPT_SSL_VERIFYPEER => 0
			));

			//POST request
			if(count($parameters) > 0) {
				curl_setopt($curl, CURLOPT_POST, 1);
				curl_setopt($curl, CURLOPT_POSTFIELDS, $parameters);
			}

			$res = curl_exec($curl);
			curl_close($curl);
			$send = SessionControl::encryptJSON($sessionid, $res);
			if(!$send) {
				$failure["data"] = "Session error";
				return $failure;
			}

			$success["data"] = $send;
			return $success;
		}

	);
	$cur = $requestChoice[$request];
	if(!isset($cur)) {
		notFound();
	}
	$ret = call_user_func($cur);

	header('Content-Type: application/json');
	echo json_encode($ret);
?>