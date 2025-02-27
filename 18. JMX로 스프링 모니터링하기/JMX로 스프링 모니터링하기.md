# JMX로 스프링 모니터링하기
* JMX(Java Management Extensions) : 자바 애플리케이션을 모니터링하고 관리하는 표준 방법
    * JMX 클라이언트 : JConsole ..etc
* MBeans(managed beans)로 알려진 컴포넌트를 노출함으로써 외부의 JMX 클라이언트는 오퍼레이션 호출, 속성 검사, MBeans의 이벤트 모니터링을 통해 애플리케이션을 관리 할 수 있음.
* JMX는 스프링 부트 애플리케이션에 기본적으로 자동 활성화됨. 이에 따라 모든 액추에이터 엔드포인트는 MBeans로 노출됨

### 액추에이터 MBeans 사용하기
* 어떤 JMX 클라이언트(ex. JConsole)를 사용해도 현재 실행중인 스프링 부트 애플리케이션의 액추에이터 엔드포인트 MBean들을 볼 수 있다.
    * JConsole은 JDK(Java Development Kit)d에 포함되어 있으며, JDK가 설치된 홈 디렉토리의 /bin 서브 디렉토리에 있을 jconsole을 실행하면 됨.
* "MBeans" 탭 클릭 -> "org.springframework.boot" 폴더 클릭 시 액추에이터 엔드포인트 MBeans가 자동 노출된다.

## 요약
* 대부분의 액추에이터 엔드포인트는 JMX 클라이언트로 모니터링할 수 있는 MBeans로 사용할 수 있음.
* 스프링은 스프링 애플리케이션 컨텍스트의 빈을 모니터링하기 위해 자동으로 JMX를 활성화 함.
* 스프링 빈에 @ManagedResource 애노테이션을 지정 시 MBeans로 노출 될 수 있음.
    * 그리고 해당 빈의 메서도에 @ManagedOperation을 지정하면 관리 오퍼레이션으로 노출될 수 있으며, 속성에 @ManagedAttribute를 지정하면 관리 속성으로 노출될 수 있음.
* 스프링 빈은 NotificationPublisher를 사용하여 JMX 클라이언트에게 알림을 전송할 수 있음.


