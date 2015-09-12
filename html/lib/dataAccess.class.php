<?php

	require_once("secret.class.php");
	require_once("signal.class.php");

	class DataAccess
	{
		private static $ip = "localhost";
		private static function getConnection() {
			return new mysqli(DataAccess::$ip, Secret::$username, Secret::$password, "mpcp");
		}
		private static function hashSalt($pass) {
			return hash("sha256", $pass.Secret::$salt);
		}
		private static function getUserId($authcode) {
			$db = self::getConnection();
			$stmt = $db->prepare("SELECT userid FROM auth WHERE authcode=? AND NOW() < expire");
			$stmt->bind_param('s', $authcode);
			$stmt->execute();
			$res = $stmt->get_result();
			$stmt->close();

			if($res->num_rows == 1) {
				return $res->fetch_assoc()['userid'];
			}
			return False;
		}
		private static function getUserIdFromUsername($username) {
			$db = self::getConnection();
			$stmt = $db->prepare("SELECT userid FROM users WHERE username=?");
			$stmt->bind_param('s', $username);
			$stmt->execute();
			$res = $stmt->get_result();
			$stmt->close();

			if($res->num_rows == 1) {
				return $res->fetch_assoc()['userid'];
			}
			return False;
		}
		private static function authQuery($authcode, $query) {
			$db = self::getConnection();
			$userid = self::getUserId($authcode);
			if(!$userid) return Signal::$authenticationError;

			$stmt = $db->prepare($query);
			$stmt->bind_param('d', $userid);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			return $stmt->get_result();
		}
		private static function accountChange($userid, $acid, $amount) {
			$db = self::getConnection();
			return !!$db->query("UPDATE accounts SET balance = balance + $amount WHERE accountid=$acid AND userid = $userid");
		}

		private static function validateTransfer($tid) {
			$db = self::getConnection();
			$stmt = $db->prepare("SELECT fromCheck, toCheck, complete FROM transfers WHERE transferid=?");
			$stmt->bind_param('d', $tid);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$res = $stmt->get_result();
			$row = $res->fetch_assoc();
			return $row['fromCheck'] && $row['toCheck'] && !$row['complete'];
		}

		private static function getOpenAccountId($userid) {
			$db = self::getConnection();
			$res = $db->query("SELECT accountid FROM accounts WHERE name='Open' AND userid=$userid");
			if(!$res)
				return Signal::$dbConnectionError;
			return $res->fetch_assoc()['accountid'];
		}

		public static function registerUser($uname, $pword) {
			$db = self::getConnection();
			$hash = self::hashSalt($pword);

			//Check if user exists
			$stmt = $db->prepare('SELECT userid FROM users WHERE username=?');
			$stmt->bind_param('s', $uname);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$res = $stmt->get_result();
			if($res->num_rows > 0)
				return Signal::$usernameTakenError;
			$stmt->close();

			//Insert user into database
			$stmt = $db->prepare('INSERT INTO users VALUES (null, ?, ?)');
			$stmt->bind_param('ss', $uname, $hash);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$stmt->close();

			$res = $db->query("SELECT LAST_INSERT_ID()");
			$uid = $res->fetch_assoc()['LAST_INSERT_ID()'];
			if(!$db->query("INSERT INTO accounts VALUES (NULL, 1, $uid, 'Open', 0)"))
				return Signal::$dbConnectionError;
			return Signal::$success;
		}

		public static function logIn($uname, $pword) {
			$db = self::getConnection();
			
			$hash = self::hashSalt($pword);

			//Check if user exists
			$stmt = $db->prepare('SELECT userid FROM users WHERE username=? AND password=?');
			$stmt->bind_param('ss', $uname, $hash);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$res = $stmt->get_result();
			$stmt->close();

			//Valid UN and pword
			if($res->num_rows == 1) {
				$row = $res->fetch_assoc();
				$userid = $row['userid'];

				//Check if user in table
				$res = $db->query("SELECT authcode FROM auth WHERE userid=$userid");

				//Generate a random authcode
				$random = openssl_random_pseudo_bytes(64);
				$authcode = self::hashSalt($random);

				if($res->num_rows >= 1) {
					$db->query("UPDATE auth SET expire DATE_ADD(NOW(), INTERVAL 1 MONTH) WHERE userid=$userid");
					$authcode = $res->fetch_assoc()['authcode'];
				} else {
					$db->query("INSERT INTO auth VALUES ( null, $userid, '$authcode', DATE_ADD(NOW(), INTERVAL 1 MONTH) )");
				}
				return new ISIGNAL($authcode, 1);
			}

			return Signal::$credentialError;
		}

		//Functions here all take authcodes
		public static function verify($authcode) {
			$userid = self::getUserId($authcode);
			if(!$userid) return Signal::$authenticationError;
			return Signal::$success;
		}

		public static function logOut($authcode) {
			$db = self::getConnection();
			$userid = self::getUserId($authcode);
			if(!$userid) return Signal::$authenticationError;

			if(!$db->query("DELETE FROM auth WHERE userid=$userid")) {
				return Signal::$dbConnectionError;
			}
			return Signal::$success;
		}

		public static function getUsername($authcode) {
			$query = "SELECT username FROM users WHERE userid=?";
			$res = self::authQuery($authcode, $query);
			if($res instanceof ISIGNAL)
				return $res;
			return new ISIGNAL($res->fetch_assoc()['username'], 1);
		}

		public static function getAccounts($authcode) {
			$query = "SELECT accountid, bankname, balance, name FROM accounts INNER JOIN banks ON accounts.bankid = banks.bankid WHERE userid=?";
			$res = self::authQuery($authcode, $query);
			if($res instanceof ISIGNAL)
				return $res;
			$rows = array();
			while($r = $res->fetch_assoc()) {
				$rows[] = $r;
			}
			return new ISIGNAL($rows, 1);
		}

		public static function getTransfers($authcode)
		{
			$all = array("requested" => array(), "received" => array());
			$rqquery = "SELECT transferid, username, amount, fromcheck, tocheck, complete, starttime, endtime FROM transfers INNER JOIN users ON transfers.toid = users.userid WHERE fromid=?";
			$res = self::authQuery($authcode, $rqquery);
			if($res instanceof ISIGNAL)
				return $res;
			while($r = $res->fetch_assoc()) {
				$all["requested"][] = $r;
			}
			
			$rcquery = "SELECT transferid, username, amount, fromcheck, tocheck, complete, starttime, endtime FROM transfers INNER JOIN users ON transfers.fromid = users.userid WHERE toid=?";
			$res = self::authQuery($authcode, $rcquery);
			if($res instanceof ISIGNAL)
				return $res;
			while($r = $res->fetch_assoc()) {
				$all["received"][] = $r;
			}
			return new ISIGNAL($all, 1);
		}

		public static function getPhones($authcode)
		{
			$query = "SELECT cellid, cellnumber FROM cellphones WHERE userid=?";
			$res = self::authQuery($authcode, $query);
			if($res instanceof ISIGNAL)
				return $res;
			$rows = array();
			while($r = $res->fetch_assoc()) {
				$rows[] = $r;
			}
			return new ISIGNAL($rows, 1);
		}

		public static function requestTransfer($authcode, $curaid, $toun, $amount) {
			$db = self::getConnection();
			$userid = self::getUserId($authcode);

			//Validate current user
			if(!$userid) 
				return Signal::$authenticationError;

			//Validate account
			$stmt = $db->prepare("SELECT balance FROM accounts WHERE accountid=? AND userid=?");
			$stmt->bind_param('dd', $curaid, $userid);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$res = $stmt->get_result();
			if($res instanceof ISIGNAL)
				return $res;
			if($res->num_rows != 1)
				return new ISIGNAL("Invalid account", 0);

			//Validate balance
			$curbal = $res->fetch_assoc()['balance'];
			$iamount = intval($amount);
			if($iamount <= 0) 
				return new ISIGNAL("Must transfer non-zero amounts", 0);
			elseif($iamount > $curbal)
				return new ISIGNAL("Transfering too much money", 0);

			//Deduct amount
			if(!self::accountChange($userid, $curaid, -$iamount))
				return Signal::$dbConnectionError;

			//Validate transferto
			$toid = self::getUserIdFromUsername($toun);
			if(!$toid)
				return new ISIGNAL("Invalid transfer destination", 0);

			//Create new transfer
			$db->query("INSERT INTO transfers VALUES (NULL, $userid, $toid, $amount, TRUE, FALSE, FALSE, NOW(), NULL)");
			$tid = $db->query("SELECT LAST_INSERT_ID()");
			return new ISIGNAL($tid->fetch_assoc()['LAST_INSERT_ID()'], 1);
		}

		public static function cancelTransfer($authcode, $tid) {
			$db = self::getConnection();
			$userid = self::getUserId($authcode);
			if(!$userid) 
				return Signal::$authenticationError;

			//Fetch correct transferid and amount
			$stmt = $db->prepare("SELECT transferid, amount, fromid FROM transfers WHERE transferid=? AND (fromid=? OR toid=?) AND complete=FALSE");
			$stmt->bind_param('ddd', $tid, $userid, $userid);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$res = $stmt->get_result();
			$stmt->close();
			if($res->num_rows != 1)
				return new ISIGNAL("Invalid transfer", 0);

			$row = $res->fetch_assoc();
			$chtid = $row['transferid'];
			$amount = $row['amount'];
			$fromid = $row['fromid'];

			//Set cancellation depending on from or to user
			$query = "";
			if($fromid == $userid)
				$query = "UPDATE transfers SET fromCheck=FALSE, complete=TRUE, endtime=NOW() WHERE transferid=$chtid";
			else
				$query = "UPDATE transfers SET toCheck=FALSE, complete=TRUE, endtime=NOW() WHERE transferid=$chtid";

			if(!$db->query($query))
				return Signal::$dbConnectionError;

			//Return amount to account
			$oid = self::getOpenAccountId($fromid);
			if($oid instanceof ISIGNAL)
				return $oid;

			if(!self::accountChange($fromid, $oid, $amount))
				return Signal::$dbConnectionError;

			return Signal::$success;
		}
		public static function acceptTransfer($authcode, $tid) {
			$db = self::getConnection();
			$userid = self::getUserId($authcode);
			if(!$userid) 
				return Signal::$authenticationError;

			//Fetch correct transferid and amount
			$stmt = $db->prepare("SELECT transferid, amount FROM transfers WHERE transferid=? AND toid=? AND complete=FALSE");
			$stmt->bind_param('dd', $tid, $userid);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$res = $stmt->get_result();
			$stmt->close();

			if($res->num_rows != 1)
				return new ISIGNAL("Invalid transfer", 0);

			$row = $res->fetch_assoc();
			$chtid = $row['transferid'];
			$amount = $row['amount'];

			//Validate transfer
			$db->query("UPDATE transfers SET toCheck=TRUE WHERE transferid=$chtid");
			if(!self::validateTransfer($tid))
				return new ISIGNAL("Invalid transfer", 0);

			//Add amount to current account
			$oid = self::getOpenAccountId($userid);
			if($oid instanceof ISIGNAL)
				return $oid;

			if(!self::accountChange($userid, $oid, $amount))
				return Signal::$dbConnectionError;

			//Mark as complete
			$db->query("UPDATE transfers SET complete=TRUE, endtime=NOW() WHERE transferid=$chtid");
			return Signal::$success;
		}

		public static function addNumber($authcode, $number) {
			$db = self::getConnection();
			$userid = self::getUserId($authcode);
			if(!$userid) 
				return Signal::$authenticationError;

			if(strlen($number) != 10)
				return new ISIGNAL("Number must be 10 digits long", 0);

			for($i = 0; $i < 9; ++$i) {
				if(!is_numeric($number[$i]))
					return new ISIGNAL("Number must be numeric 0 - 9 only", 0);
			}

			//Check if number already exists
			$stmt = $db->prepare("SELECT cellid FROM cellphones WHERE cellnumber=?");
			$stmt->bind_param('s', $number);

			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$res = $stmt->get_result();
			$stmt->close();

			if($res->num_rows > 0) {
				return new ISIGNAL("Phone number already added", 0);
			}

			$stmt = $db->prepare("INSERT INTO cellphones VALUES (NULL, $userid, ?)");
			$stmt->bind_param('s', $number);

			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$stmt->close();
			return Signal::$success;
		}

		public static function deleteNumber($authcode, $cellid) {
			$db = self::getConnection();
			$userid = self::getUserId($authcode);
			if(!$userid) 
				return Signal::$authenticationError;

			$stmt = $db->prepare("DELETE FROM cellphones WHERE cellid=? AND userid=$userid");
			$stmt->bind_param('d', $cellid);

			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$stmt->close();
			return Signal::$success;
		}

		public static function getUserFromPhone($number) {
			$db = self::getConnection();

			$stmt = $db->prepare("SELECT username FROM cellphones INNER JOIN users ON cellphones.userid = users.userid WHERE cellnumber=?");
			$stmt->bind_param('s', $number);

			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$res = $stmt->get_result();
			$stmt->close();

			return new ISIGNAL($res->fetch_assoc()["username"], 1);
		}

		public static function createAccount($authcode) {

		}
	}
?>