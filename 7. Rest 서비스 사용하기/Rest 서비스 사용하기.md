# Rest 서비스 사용하기
* API : 데이터를 받거나 제공한다.
* 스프링에서 REST API를 사용하는 방법
    * RestTemplate : 스프링 프레임워크에서 제공하는 간단하고 동기화된 REST 클라이언트
    * Traverson : 스프링 HATEOAS에서 제공하는 하이퍼링크를 인식하는 동기화 REST 클라이언트로 같은 이름의 자바스크립트 라이브러리로부터 비롯된 것
    * WebClient : 스프링 5에서 소개된 반응형 비동기 REST 클라이언트
<br><br>
---

### RestTemplate으로 REST 엔트포인트 사용하기
* 저수준의 HTTP 라이러리로 작업하면서 클라이언트는 클라이언트 인스턴스와 요청 객체를 생성하고, 해당 요청을 실행하고, 응답을 분석하여 관련 도메인 객체와 연관시켜 처리해야 한다. 또한 그 와중에 발생될 수 있는 예외도 처리해야 함.
* RestTemplate : REST 리소스를 사용하는데 번잡한 일을 처리해준다.
    * REST 리소스와 상호작용하기 위한 주요 메서드
        <table>
        <tr><th>메서드</th><th>기능 설명</th></tr>
        <tr><td>delete(...)</td><td>지정된 URL의 리소스에 HTTP DELETE 요청 수행</td></tr>
        <tr><td>exchange(...)</td><td>지정된 HTTP 메서드를 URL에 대해 실행하며, 응답 몸체와 연결되는 객체를 포함하느 ResponseEntity를 반환한다.</td></tr>
        <tr><td>execute(...)</td><td>지정된 HTTP 메서드를 URL에 대해 실행하며, 응답 몸체와 연결되는 객체를 반환한다.</td></tr>
        <tr><td>getForEntity(...)</td><td>HTTP GET 요청을 전송하며, 응답 몸체와 연결되는 객체를 포함하는 ResponseEntity를 반환한다.</td></tr>
        <tr><td>getForObject(...)</td><td>HTTP GET 요청을 전송하며, 응답 몸체와 연결되는 객체를 반환한다.</td></tr>
        <tr><td>headForHeaders(...)</td><td>HTTP HEAD 요청을 전송하며, 지정된 리소스 URL의 HTTP 헤더를 반환한다.</td></tr>
        <tr><td>optionsForAllow(...)</td><td>HTTP OPTIONS 요청을 전송하며, 지전된 URL의 Allow 헤더를 반환한다.</td></tr>
        <tr><td>patchForObject(...)</td><td>HTTP PATCH 요청을 전송하며, 응답 몸체와 연결되는 결과 객체를 반환한다.</td></tr>
        <tr><td>postForEntity(...)</td><td>URL에 데이터를 POST하며, 응답 몸체와 연결되는 객체를 포함하는 ResponseEntity를 반환한다.</td></tr>
        <tr><td>postForLocation(...)</td><td>URL에 데이터를 POST하며, 새로 생성된 리소스의 URL을 반환한다.</td></tr>
        <tr><td>postForObject(...)</td><td>URL에 데이터를 POST하며, 응답 몸체와 연결되는 객체를 반환한다.</td></tr>
        <tr><td>put(...)</td><td>리소스 데이터를 지정된 URL에 PUT한다.</td></tr>
        </table>
    > 오버로딩 형태
    > * 가변 인자 리스트에 지정된 URL 매개변수에 URL 문자열(String 타입)을 인자로 받는다.
    > * Map<String, String>에 지정된 URL 매개변수에 URL문자열을 인자로 받는다.
    > * java.net.URI를 URL에 대한 인자로 받으며, 매개변수화된 URL은 지원하지 않는다. 

    * RestTemplate은 TRACE를 제외한 표준 HTTP 메서드 각각에 대해 최소한 하나의 메서드를 갖고 있다. 그리고 execute()와 exchange()는 모든 HTTP 메서드의 요청을 전송하기 위한 저수준의 범용 메서드를 제공한다.

    * RestTemplate 사용방법
        * 방법 1 : RestTemplate 인스턴스 생성하기
            ```java
            RestTemplate rest = new RestTemplate();
            ```
        * 방법 2 : 빈으로 선언하고 필요할 때 주입하기
            ```java
            @Bean
            public RestTemplate restTemplate(){
                return new RestTemplate();
            }
            ```

##### 리소스 가져오기(GET)
* getForObject() 사용
    ```java
    // 방법 1 : id값 직접 넣어주기
    public Ingredient getIngredientById(String ingredientId){
        return rest.getForObject("http://localhost:8080/ingredients/{id}", // URL 문자열
        Ingredient.class, // 응답이 바인되는 타입 => 여기서는 JSON 형식인 응답데이터가 객체로 역직렬화되어 반환된다.
        ingredientId); // URL의 id 부분에 들어갈 변수
    }

    // 방법 2 : Map을 사용해서 URL 변수들을 지정
    public Ingredient getIngredientById(String ingredientId){
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("id", ingredientId);
        return rest.getForObject("http://localhost:8080/ingredients/{id}", // URL 문자열
        Ingredient.class, // 응답이 바인되는 타입 => 여기서는 JSON 형식인 응답데이터가 객체로 역직렬화되어 반환된다.
        urlVariables); // URL의 변수들 => 키가 "id"인 값을 변경해줌.
    }

    // 방법 3 : URI 매개변수를 사용할 때는 URI 객체를 구성하여 getForObject()를 호출해야 함.
    public Ingredient getIngredientById(String ingredientId){
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("id", ingredientId);
        URI url = UriComponentsBuilder
                    .fromHttpUrl("http://localhost:8080/ingredients/{id}")
                    .build(urlVariables);
        return rest.getForObject(url, Ingredient.class);
    }
    ```
* getForEntity() 사용 : 도메인 객체를 포함하는 ResponseEntity 객체를 반환함. (ResponseEntity에는 응답헤더와 같은 더 상세한 응답 콘텐츠가 포함될 수 있다.)
    * getForEntity()는 getForObject()와 동일한 매개변수를 갖도록 오버로딩됨.
    * => 즉, URL 변수들을 가변 인자 리스트나 URI 객체로 전달하여 getForEntity()를 호출할 수 있음.
    ```java
    public Ingredient getIngredientById(String ingredientId){
        ResponseEntity<Ingredient> responseEntity = 
            rest.getForEntity("http://localhost:8080/ingredients/{id}",
                Ingredient.class,
                ingredientId
            );
        log.info("Fetched time : "+ responseEntity.getHeaders().getDate());
        return responseEntity.getBody();
    }
    ```

##### 리소스 쓰기(PUT)
* put() 사용하기
    * 특정 식자재 리소스를 새로운 Ingredient 객체의 데이터로 교체하기
    ```java
    public void updateIngredient(Ingredient ingredient){ // 반환값은 void
        rest.put("http://localhost:8080/ingredients/{id}",
            ingredient, // 객체 자체를 전송
            ingredient.getId() // {id}에 들어갈 매개변수
        );
    }
    ```

##### 리소스 삭제하기(DELETE)
* delete() 사용하기
    ```java
    public void deleteIngredient(Ingredient ingredient){ // 반환값은 void
        rest.delete("http://localhost:8080/ingredients/{id}",
            ingredient.getId() // {id}에 들어갈 매개변수
        );
    }
    ```

##### 리소스 데이터 추가하기(POST)
* postForObject() 사용하기
    * 새로운 식자재를 추가하기
    ```java
    public Ingredient createIngredient(Ingredient ingredient){
        return rest.postForObject("http://localhost:8080/ingredients",
            ingredient, // 서버에 전송될 객체
            Ingredient.class // 이 객체의 타입
            // 혹시나 URL 변수값을 갖는 Map이나 URL을 대체할 가변 매개변수 리스트를 4번째 매개변수로 전달할 수 있음.
        );
    }
    ```
* postForLocation() 사용하기
    * 클라이언트에서 새로 생성된 리소스의 위치가 추가로 필요할 때
    ```java
    public URI createIngredient(Ingredient ingredient){ // 새로 생성된 리소스 대신 새로 생성된 리소스의 URI를 반환한다는 것이 다름. => 반환된 URI는 해당 응답의 Location헤더에서 얻음.
        return rest.postForLocation("http://localhost:8080/ingredients",
            ingredient
        );
    }
    ```
* postForEntity() 사용하기
    * 새로 생성된 리소스의 위치와 리소스 객체 모두가 필요할 때
    ```java
    public Ingredient createIngredient(Ingredient ingredient){
        ResponseEntity<Ingredient> responseEntity = rest.postForEntit("http://localhost:8080/ingredients",
            ingredient, // 서버에 전송될 객체
            Ingredient.class // 이 객체의 타입
        );

        log.info("New resource created at " + responseEntity.getHeaders().getLocation());

        return responseEntity.getBody();
    }
    ```
<br><br>
---

### Traverson으로 REST API 사용하기
* 우리가 사용하는 API에서 하이퍼링크를 포함해야할 때 사용
* Traverson : 스프링 데이터 HATEOAS에 같이 제공되며, 스프링 애플리케이션에서 하이퍼 미디어 API를 사용할 수 있는 솔루션
    * Traverson의 뜻 : '돌아다닌다(traverse on)'이며, 여기서는 관계 이름으로 원하는 API를 이동하며 사용할 것이다.
* 사용방법
    1. 해당 API의 기본 URI를 갖는 객체를 생성해야 함.
        ```java
        Traverson traverson = new Traverson(URI.create("http://localhost:8080/api"), MediaTypes.HAL_JSON);
        ```
        * 이후부터는 각 링크의 관계 이름으로 API를 사용한다.
        * Traverson 생성자에는 해당 API가 HAL 스타일의 하이퍼링크를 갖는 JSON 응답을 생성한다는 것을 인자로 지정할 수도 있음. => 이 인자를 지정하는 이유 : 수신되는 리소스 데이터를 분석하는 방법을 Traverson이 알 수 있게 하기 위해서
        * Traverson이 필요할 떄는 RestTemplate 처럼 Traverson 객체를 생성한 후에 사용하거나 또는 주입되는 빈으로 선언할 수 있음.
    2. 링크를 따라가면서 API를 사용하기
        ```java
        ParameterizedTypeReference<Resources<Ingredient>> ingredientType = new ParameterizedTypeReference<Resources<Ingredient>>() {};

        Resources<Ingredient> ingredientRes = traverson
            .follow("ingredients") // 리소스 링크의 관계 이름이 "ingredients"인 리소스로 이동할 수 있음.
            .toObject(ingredientType); // 해당 리소스의 콘텐츠가져오는 역할로, 이 때 데이터를 읽어 들이는 객체의 타입을 지정해야함.

        Collection<Ingredient> ingredients = ingredientRes.getContent();
        ```
        * toObject() 사용 시 타입을 지정해야한다. 이 때, <code>Resources&lt;Ingredient&gt;</code> 타입의 객체로 읽어들여야 하는데, 자바에서는 런타임 시에 제네릭 타입의 타입 정보(<code>&lt;Ingredient&gt;</code>부분)가 소거되어 리소스 타입을 지정하기 어렵다. => 하지만, ParameterizedTypeReference를 생성하면 리소스 타입을 지정할 수 있다. 

        * ex) 가장 최근에 생성된 타코들을 가져오기
            ```java
            ParameterizedTypeReference<Resources<Taco>> tacoType = new ParameterizedTypeReference<Resources<Taco>>() {};

            Resources<Taco> tacoRes = traverson
                .follow("tacos", "recents") // .follow()는 한번만 호출할 수 있다.
                .toObject(tacoType); 

            Collection<Taco> tacos = tacoRes.getContent();
            ```
* Traverson은 HATEOAS가 활성화된 API를 이동하며 리소스를 쉽게 가져올 수 있지만, 리소스의 생성 및 삭제 메서드는 제공하지 x.
* RestTemplate은 리소스의 생성 및 삭제는 제공하지만, API를 이동하는 것은 쉽지 않다.
* RestTemplate + Traverson : 리소스의 변경이나 삭제 기능 + API 이동
    ```java
    // 새로운 Ingredient를 추가
    private Ingredient addIngredient(Ingredient ingredient){
        // 1. Traverson으로 URL 가져오기
        String ingredientsUrl = traverson
            .follow("ingredients") // ingredients 링크 따라가기
            .asLink() // ingredients 링크 자체를 요청
            .getHref(); // 이 링크의 URL을 가져오기
        
        // 2. RestTemplate으로 1에서 가져온 URL로 생성요청 보내기
        return rest.postForObject(ingredientsUrl, ingredient, Ingredient.class);
    }

    ```

## 요약
* 클라이언트는 RestTemplate을 사용해서 REST API에 대한 HTTP 요청을 할 수 있다.
* Traverson을 사용하면 클라이언트가 응답에 포함된 하이퍼링크를 사용해서 원하는 API로 이동할 수 있다.