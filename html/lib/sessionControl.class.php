<?php
	
	require_once("protocol.class.php");
	require_once("secret.class.php");

	class SessionControl
	{
		private static $ip = "localhost";

		private static function getConnection() {
			return new mysqli(self::$ip, Secret::$username, Secret::$password, "mpcp");
		}

		private static function decrypt($key, $data) {
			$iv = base64_decode($data["iv"]);
			$encrypted = base64_decode($data["encrypted"]);
			$key = base64_decode($key);

			$decrypted = openssl_decrypt($encrypted, 'AES-128-CBC', $key, OPENSSL_RAW_DATA, $iv);
			return $decrypted;
		}

		private static function sessionQuery($sessionid, $query) {
			$db = self::getConnection();

			$stmt = $db->prepare($query);
			$stmt->bind_param('d', $sessionid);
			$stmt->execute();
			return $stmt->get_result();
		}

		public static function initHandshake($protocol) {
			$db = self::getConnection();
			$stmt = $db->prepare("SELECT algid FROM kap WHERE name=?");
			$stmt->bind_param('s', $protocol);
			$stmt->execute();
			$res = $stmt->get_result();
			$stmt->close();

			if($res->num_rows != 1) {
				return False;
			}

			//Create session
			$algid = $res->fetch_assoc()["algid"];
			$db->query("INSERT INTO session VALUES (NULL, $algid, 1, NULL, 0, NULL, DATE_ADD(NOW(), INTERVAL 1 DAY), 0 )");

			//Return sessionid
			$res = $db->query("SELECT LAST_INSERT_ID()");
			return $res->fetch_assoc()['LAST_INSERT_ID()'];
		}

		public static function execute($sessionid, $step, $data) {
			$db = self::getConnection();
			$query = "SELECT sessionid, name, length, step, tempdata FROM session INNER JOIN kap ON session.algid = kap.algid WHERE sessionid=? AND NOW()<expire AND start=0";
			$res = self::sessionQuery($sessionid, $query);

			if($res->num_rows != 1) {
				return False;
			}

			$res = $res->fetch_assoc();
			$ssid = intval($res["sessionid"]);
			$pname = $res["name"];
			$curstep = $res["step"];
			$repstep = intval($step);
			$maxstep = $res["length"];
			$tempdata = $res["tempdata"];

			//Check correct step
			if($repstep - 1 != $curstep)
				return False;

			//Terminate protocol
			if($repstep == $maxstep + 2) {
				return self::endHandshake($ssid, $step, $data);
			}

			$pstep = $repstep - 2; //0th step is first

			$protocol = Protocol::$$pname;
			$res = call_user_func($protocol[$pstep], $data, $tempdata);

			++$pstep; ++$curstep;
			$store = $res["store"];
			$send = $res["send"];
			if($pstep == $maxstep) {
				$db->query("UPDATE session SET symkey='$store', tempdata=NULL, step=$curstep WHERE sessionid=$ssid");
			} else {

			}
			return $send;
		}

		public static function endHandshake($sessionid, $step, $data) {
			$db = self::getConnection();

			$query = "SELECT symkey FROM session WHERE sessionid=?";
			$res = self::sessionQuery($sessionid, $query);
			$key = $res->fetch_assoc()["symkey"];

			$decrypted = self::decrypt($key, $data);
			
			if($decrypted != "Confirm")
				return False;

			//Handshake complete
			$db->query("UPDATE session SET step=$step, start=1 WHERE sessionid=$sessionid");
			return "Success";
		}

		public static function decryptJSON($sessionid, $data) {
			$query = "SELECT symkey FROM session WHERE sessionid=? AND NOW()<expire AND start=1 AND terminate=0";
			$res = self::sessionQuery($sessionid, $query);

			if(!$res)
				return $res;

			$key = $res->fetch_assoc()["symkey"];

			$decrypted = self::decrypt($key, $data);
			return json_decode($decrypted, true);
		}

		public static function encryptJSON($sessionid, $data) {
			$query = "SELECT symkey FROM session WHERE sessionid=? AND NOW()<expire AND start=1 AND terminate=0";
			$res = self::sessionQuery($sessionid, $query);
			if(!$res)
				return $res;

			$key = $res->fetch_assoc()["symkey"];
			$key = base64_decode($key);

			$iv = openssl_random_pseudo_bytes(16);
			$encrypted = openssl_encrypt($data, 'AES-128-CBC', $key, OPENSSL_RAW_DATA, $iv);

			$ret = array("iv" => base64_encode($iv), "encrypted" => base64_encode($encrypted));
			return $ret;
		}

	}
?>