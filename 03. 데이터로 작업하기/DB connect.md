# 데이터로 작업하기
* 데이터 퍼시스턴스(persistence) : 저장 및 지속성 유지
* 상용구 코드(boilerplate code) : 언어의 문법이난 형식 등의 이유로 거의 수정 없이 여러 곳에 반복적으로 사용해야 하는 코드
* 데이터 퍼시스턴스의 최우선 선택 : 관계형 DB + SQL
* 관계형 데이터 사용 방법 : JDBC, JPA
<br><br>
---

### JDBC를 사용해서 데이터 읽고 쓰기
* SQLException : catch 블록으로 반드시 처리해야 하는 checked 예외
* JbcTemplate의 queryForObject() : 쿼리를 수행함
* JbcTemplate의 mapRowToIngredient() : 쿼리 결과를 Ingredient 객체로 생성함
* 적용
    * build.gradle : <code>implementation 'org.springframework.boot:spring-boot-starter-jdbc'</code>
    * model
        ```java
        @Data
        public class Order {

            private Long id;
            private Date placedAt;
            ...
        }
        ```
    * repository
        ```java
        import com.hji.spring.model.Ingredient;

        public interface IngredientRepository {
            Iterable<Ingredient> findAll();
            Ingredient findById(String id);
            Ingredient save(Ingredient ingredient);
        }

        // =============================================================

        @Repository
        public class JdbcIngredientRepository implements IngredientRepository {
            private JdbcTemplate jdbc;
            
            @Autowired
            public JdbcIngredientRepository(JdbcTemplate jdbc) {
                this.jdbc = jdbc;
            }
            
            @Override
            public Iterable<Ingredient> findAll() {
                return jdbc.query("select id, name, type from Ingredient",
                        this::mapRowToIngredient);
            }
            
            @Override
            public Ingredient findById(String id) {
                return jdbc.queryForObject(
                        "select id, name, type from Ingredient where id=?",
                        this::mapRowToIngredient, id);
            }
            
            private Ingredient mapRowToIngredient(ResultSet rs, int rowNum)
                    throws SQLException {
                return new Ingredient(
                        rs.getString("id"),
                        rs.getString("name"),
                        Ingredient.Type.valueOf(rs.getString("type")));
            }
            
            @Override
            public Ingredient save(Ingredient ingredient) {
                jdbc.update(
                        "insert into Ingredient (id, name, type) values (?, ?, ?)",
                        ingredient.getId(),
                        ingredient.getName(),
                        ingredient.getType().toString());
                return ingredient;
            }
        }
        ```
    * controller
        ```java
        @Slf4j // 컴파일 시에 Lombok에 제공됨 => log 사용 가능
        @Controller // 컨트롤러로 식별되게 함, 스프링 애플리케이션 컨텍스트의 빈으로 이 클래스의 인스턴스를 자동 생성한다.
        @RequestMapping("/design") // 해당 경로의 요청을 처리함.
        public class DesignTacoController {

            private final IngredientRepository ingredientRepository;

            @Autowired
            public DesignTacoController(IngredientRepository ingredientRepository){
                this.ingredientRepository = ingredientRepository;
            }

            @GetMapping
            public String showDesignForm(Model model) { // Model : 컨트롤러와 뷰 사이에서 데이터를 운반하는 객체 -> Model 객체의 속성에 있는 데이터는 뷰가 알 수 있는
                                                        // 서블릿 요청 속성들로 복사된다.

                List<Ingredient> ingredients = new ArrayList<>();
                ingredientRepository.findAll().forEach(i -> ingredients.add(i));

                Type[] types = Ingredient.Type.values();
                for (Type type : types) {
                    model.addAttribute(type.toString().toLowerCase(),
                            filterByType(ingredients, type));
                }

                model.addAttribute("taco", new Taco());
                return "design";

            }
            ....
        }
        ```
    * schema 정의서 : classpath 루트 경로(src/main/resources/schema.sql)에 있으면 애플리케이션이 시작될 때 schema.sql 파일의 SQL이 사용 중인 DB에서 자동 실행됨.


<br><br>
---

### 스프링 데이터 JPA를 사용해서 데이터 저장하고 사용하기
* 스프링 데이터 DB : 스프링 데이터에서는 리포지토리 인터페이스를 기바능로 이 인터페이스를 구현하는 리포지토리를 자동 생성해줌.
    * 스프링 데이터 JPA : 관계형 DB의 JPA 퍼시스턴스
    * 스프링 데이터 MongoDB : 몽고 문서형 DB의 퍼시스턴스
    * 스프링 데이터 Neo4 : Neo4j 그래프 DB의 퍼시스턴스
    * 스프링 데이터 레디스(Redis) : 레디스 키-값 스토어의 퍼시스턴스
    * 스프링 데이터 카산드라(Cassandra) : 카산드라 DB의 퍼시스턴스

* 적용
    * build.gradle : <code>implementation 'org.springframework.boot:spring-boot-starter-data-jpa'</code>
    * model
        ```java
        import jakarta.persistence.Entity;
        import jakarta.persistence.GeneratedValue;
        import jakarta.persistence.GenerationType;
        import jakarta.persistence.Id;
        import jakarta.persistence.ManyToMany;
        import jakarta.persistence.PrePersist;
        import jakarta.persistence.Table;
        import jakarta.validation.constraints.Digits;
        import jakarta.validation.constraints.NotBlank;
        import jakarta.validation.constraints.Pattern;
        import lombok.Data;

        @Data
        @Entity
        @Table(name = "Taco_Order") // order이라는 예약어로 사용되게 하지 않기 위해 
        public class Order implements Serializable{

            private static final long serialVersionUID = 1L;

            @Id
            @GeneratedValue(strategy = GenerationType.AUTO)
            private Long id;
            private Date placedAt;

            @NotBlank(message="Name is required")
            private String deliveryName;
            
            @NotBlank(message="Street is required")
            private String deliveryStreet;
            
            @NotBlank(message="City is required")
            private String deliveryCity;
            
            @NotBlank(message="State is required")
            private String deliveryState;
            
            @NotBlank(message="Zip code is required")
            private String deliveryZip;
            
            @CreditCardNumber(message="Not a valid credit card number")
            private String ccNumber;
            
            @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
                    message="Must be formatted MM/YY")
            private String ccExpiration;
            
            @Digits(integer=3, fraction=0, message="Invalid CVV")
            private String ccCVV;

            @ManyToMany(targetEntity=Taco.class)
            private List<Taco> tacos = new ArrayList<>();
            
            public void addDesign(Taco design) {
                this.tacos.add(design);
            }
            
            @PrePersist
            void placedAt() {
                this.placedAt = new Date();
            }
        }
        ```
    * repository
        * JPA의 장점 : 애플리케이션이 시작될 때 스프링 데이터 JPA가 각 인터페이스 구현체(클래스)를 자동으로 생성해 준다
        ```java
        import org.springframework.data.repository.CrudRepository;

        import com.hji.spring.model.Order;

        public interface OrderRepository extends CrudRepository<Order, String>{ // CrudRepository 인터페이스에는 DB의 CRUD 연산을 위한 많은 메서드가 선언되어 있다.
        }
        ```
## 요약
* 스프링의 JdbcTemplate는 JDBC 작업을 굉장히 쉽게 해준다.
    * DB가 생성해주는 ID 값을 알아야 할 때 : PreparedStatementCreator, KeyHolder 사용
    * 데이터 추가 시 : SimpleJdbcInsert 사용
* 스프링 데이터 JPA는 리포지토리 인터페이스를 작성하듯이 JPA 퍼시스턴스를 쉽게 해준다.