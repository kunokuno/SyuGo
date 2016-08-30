<?php
//-------------------
//ユーザ登録
//-------------------
	require "database_connect.php";
	//$id=htmlspecialchars($_GET['id']);
	$code=htmlspecialchars($_GET['code']);
	$name=htmlspecialchars($_GET['name']);
	$alt=htmlspecialchars($_GET['alt']);
	$lat=htmlspecialchars($_GET['lat']);
	$lan=htmlspecialchars($_GET['lan']);
	$accuracy=htmlspecialchars($_GET['accuracy']);
	$etime=htmlspecialchars($_GET['etime']);

//現在時刻を取得してgettimeに格納
	$date = new DateTime('now', new DateTimeZone('Asia/Tokyo'));
	$gettime= $date->format('Y-m-d H:i:s');
	$stmt = $pdo->prepare("INSERT INTO User (id,code,name,alt,lat,lan,gettime,accuracy,etime)VALUES (:id,:code,:name,:alt,:lat,:lan,:gettime,:accuracy,:etime)");
			$null=null;
			$stmt->bindParam(':id', $null, PDO::PARAM_NULL);
			$stmt->bindValue(':code', $code, PDO::PARAM_STR);
			$stmt->bindParam(':name', $name, PDO::PARAM_STR);
			$stmt->bindParam(':alt', $alt, PDO::PARAM_STR);
			$stmt->bindParam(':lat', $lat, PDO::PARAM_STR);
			$stmt->bindValue(':lan', $lan, PDO::PARAM_STR);
			$stmt->bindValue(':gettime', $gettime, PDO::PARAM_STR);
			$stmt->bindValue(':accuracy', $accuracy, PDO::PARAM_STR);
			$stmt->bindValue(':etime', $etime, PDO::PARAM_STR);
			$flag=$stmt->execute();
				if(!$flag){
					//データ送信エラー
					echo"0";
					exit();
				}else{
					//登録完了
					echo "1";
					exit();
			}

?>