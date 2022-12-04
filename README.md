# 자바 소켓통신을 이용한 채팅프로그램 개발
----------------------

## <img width="30" height="30" src="https://user-images.githubusercontent.com/95892601/204138950-40ec2ebd-fb00-4e3b-b3db-75ba7fe8f156.png">시연영상
<ul>
  <li>🎥<a href="https://www.youtube.com/watch?v=ZL1rfqkIQOM">시연영상 바로가기</a></li>
</ul>
<br/>

---------------------

## 소개
자바 소켓통신을 이용한 채팅프로그램을 구현하였습니다.</br>
UI는 자바 swing을 이용하여 구현하였고 데이터베이스는 오라클을 이용하였습니다.
<br/>



![채팅창 로그인 화면](https://user-images.githubusercontent.com/95892601/178210184-d00d07aa-1525-4713-91f1-b18a97913333.png)
![채팅창 화면](https://user-images.githubusercontent.com/95892601/178210275-f066e87e-e571-4487-bdc1-677073d987aa.png)

----------------------


## 개발환경
![image](https://user-images.githubusercontent.com/95892601/178211276-5a86ae48-e39d-46f5-a5f9-e7917aa1c163.png)

----------------------

# 주요 기능

### 클라이언트 기능

#### 1.가입하기


![image](https://user-images.githubusercontent.com/95892601/178213284-61b29003-c041-4710-a4aa-1b75ed626d98.png)


>오라클과 연동되어 아이디 중복체크 및 회원 가입 구현


#### 2.단체대화방


![image](https://user-images.githubusercontent.com/95892601/178213857-3ccf624b-6ec4-4c7c-a4a0-ade2d1a5c9b0.png)


>로그인 후 단체대화방으로 입장하여 모든 사용자의 대화내용을 볼 수 있는 단체톡방 구현


#### 3.1대1 대화


![image](https://user-images.githubusercontent.com/95892601/178214162-b2fb082e-e15d-4682-925f-0132201eb5fc.png)


>1대1 대화상대를 선택 후 대화 신청을 누르면 선택된 사용자 닉네임을 가진 클라이언트에게 1대1 대화신청이 보내집니다.
해당 요청을 받은 사용자는 수락 및 거절을 할 수 있습니다.


##### <수락 시 개인톡방 진입>


![image](https://user-images.githubusercontent.com/95892601/178214438-6b2c369e-33ed-4628-8a8b-8a744c0fb85f.png)


##### <개인톡방 퇴장 시 화면>


![image](https://user-images.githubusercontent.com/95892601/178214576-76979629-563b-4684-86f1-ea2c58ae3c17.png)



##### <거절 시 화면>

![image](https://user-images.githubusercontent.com/95892601/178214616-52530c01-88f4-4a7f-a366-385976747701.png)


#### 4. 개인정보 수정


![image](https://user-images.githubusercontent.com/95892601/178216200-b54363f8-7fa5-46bf-b4bc-c71b333bb875.png)


>회원정보 수정을 클릭하면 먼저 현재 비밀번호를 입력하여야 합니다.
>비밀번호가 확인이 되면 어떤 데이터를 수정할 것인지 선택 후 선택된 데이터 작성하여 변경하기 클릭하면 오라클의 member테이블이 수정됩니다.


##### <닉네임 수정 시 화면> 


![image](https://user-images.githubusercontent.com/95892601/178216708-2f3de7d8-28ed-4629-ac90-629897d3b263.png)


### 서버기능

#### 1.로그저장


![image](https://user-images.githubusercontent.com/95892601/178218222-98b6ab70-ef1e-4483-8695-cf338d8ae976.png)


>서버에서는 접속한 사용자들의 로그를 관리 할 수 있도록 로그를 txt파일 형식으로 저장할 수 있게 구현 하였습니다.

##### <저장된 로그 저장 화면>


![image](https://user-images.githubusercontent.com/95892601/178218569-10198cec-aaa0-488a-aa0a-469875e21373.png)

#### 2.공지사항 알림

![image](https://user-images.githubusercontent.com/95892601/178218849-815f4cca-efd3-4258-a313-818e66dad927.png)


> 서버에서 공지사항 알림을 클릭후 메시지를 입력하면 모든 클라이언트들에게 공지사항 메시지를 보냅니다.


##### <공지사항 알림 결과 화면>


![image](https://user-images.githubusercontent.com/95892601/178219059-14af50ec-2769-4793-99ca-f790298225ac.png)


#### 3.회원조회

![image](https://user-images.githubusercontent.com/95892601/178219462-5b9758b2-35f5-4160-b844-aadcb7ef9c8d.png)


>콤보박스에 아이디, 이름, 전화번호, 주소중 하나를 선택하여 해당하는 조건의 회원을 검색 및 조회할 수 있습니다..


#### 4.현재 접속자 및 강퇴처리

![image](https://user-images.githubusercontent.com/95892601/178219708-d4340f7f-03e9-42e4-af99-bbf9a333dd81.png)


>현재 접속자 버튼을 누르면 현재 접속한 인원의 ip 및 접속시간을 볼 수 있습니다.
>또한 강퇴할 사용자를 선택 후 클라이언트 접속끊기 버튼을 누르면 해당 사용자는 "운영자에 의해 강퇴되었습니다"라는 메시지를 받고 접속이 종료 됩니다.


##### <강퇴 처리 시 사용자 화면>


![image](https://user-images.githubusercontent.com/95892601/178220009-4ebd38f6-754a-4bf5-b536-fdaef92af836.png)




# Document

1.공정표
![image](https://user-images.githubusercontent.com/95892601/178211340-61e3afbb-749e-4bac-a3aa-60511ef0d812.png)

----------------------

2.요구사항 정의서
![image](https://user-images.githubusercontent.com/95892601/178211438-6ffd5c3e-8883-4110-b857-ab11ec72cc72.png)

----------------------
3. class 설계
![image](https://user-images.githubusercontent.com/95892601/178212599-c37feb03-7b84-4aa9-8be9-2323ec38cee7.png)

4. ERD 설계
![image](https://user-images.githubusercontent.com/95892601/178212773-419ac6fd-be14-4b01-80df-c76660b1437a.png)


