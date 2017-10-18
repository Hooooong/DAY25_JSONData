# HttpConnection 예제

### 설명
____________________________________________________

![HTTPConnection](https://github.com/Hooooong/DAY25_JSONData/blob/master/image/JsonData.gif)

- HTTPConnection 예제
- URL([https://api.github.com/users](https://api.github.com/users)) 에 있는 데이터 (JSON 형식) 를 Parsing 하여 RecyclerView 에 출력

### KeyPoint
____________________________________________________

- HTTPConnection

  - 참조 : [HTTPConnection](https://github.com/Hooooong/DAY25_HTTPConnect#httpconnection)

- AsyncTask 사용

  - 참조 : [AsyncTask](https://github.com/Hooooong/DAY25_HTTPConnect#asynctask)

- Glide (Image Loader Library)

  - URL 이미지를 ImageView 에 그려주기 위해서는 Bitmap 으로 변환하여 넣어야 한다.

  ```java
  public static Bitmap getImage(String src) {
      Bitmap bitmap = null;
      try {
          // Network 처리
          URL url = new URL(src);
          HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
          urlConnection.setRequestMethod("GET");

          // 통신이 성공적인지 체크
          if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

              InputStream is = urlConnection.getInputStream();
              // url Stream 을 통해 Bitmap 으로 변환
              bitmap = BitmapFactory.decodeStream(is);

              is.close();
          } else {
              Log.e("ServerError", urlConnection.getResponseCode() + " , " + urlConnection.getResponseMessage());
          }
          urlConnection.disconnect();
      } catch (Exception e) {
          Log.e("Error", e.toString());
      }

      return bitmap;
  }
  ```

  - RecyclerView 경우 Scroll 을 할 때마다 `onBindViewHolder` 를 호출하기 때문에 AsyncTask 로 처리하기에 너무 많은 작업이 필요하다 (Cache 등)

  ```java
  @Override
  public void onBindViewHolder(final Holder holder, int position) {
      // ViewHolder 가 Bind 될 때마다 AsyncTask 가 new 가 되고, Sub Thread 가 실행이 된다.
      holder.setImageView(user.getAvatar_url());
  }

  public void setImageView(final String imageUrl) {
      new AsyncTask<Void, Void, Bitmap>(){
          @Override
          protected void onPreExecute() {
              super.onPreExecute();
          }

          @Override
          protected Bitmap doInBackground(Void... voids) {
              return Remote.getImage(imageUrl);
          }

          @Override
          protected void onPostExecute(Bitmap bitmap) {
              //JSONString 을 Parsing 하여 List 에 넣어둔다.
              imageView.setImageBitmap(bitmap);
          }
      }.execute();
  }
  ```

  - 이러한 작업들을 처리해주고, Image 를 좀 더 빠르게 Load 할 수 있게 나온 Library 가 `Glide` 이다.

  - Gradle 설정

  ```Gradle
  complie 'com.github.bumptech.glide:glide:3.+'
  ```

  - 사용 방법

  ```java
  // Glide 실행 방법
  Glide.with(context)
        .load(imageUrl)
        .into(imageView);
  ```

  - 참조 : [Glide](https://github.com/bumptech/glide), [Android의 이미지로딩 라이브러리](http://d2.naver.com/helloworld/429368)

- JSON ( JavaScript Object Notation )

  > JSON(JavaScript Object Notation)은 속성-값 쌍으로 이루어진 데이터 오브젝트를 전달하기 위해 인간이 읽을 수 있는 텍스트를 사용하는 개방형 표준 포맷이다. 비동기 브라우저/서버 통신 (AJAX)을 위해, 넓게는 XML(AJAX가 사용)을 대체하는 주요 데이터 포맷이다. 특히, 인터넷에서 자료를 주고 받을 때 그 자료를 표현하는 방법으로 알려져 있다. 자료의 종류에 큰 제한은 없으며, 특히 컴퓨터 프로그램의 변수값을 표현하는 데 적합하다

  - JSON 형식

  ```JSON
  [
    {
       "이름": "테스트1",
       "나이": 25,
       "성별": "여",
       "주소": "서울특별시 양천구 목동",
       "특기": ["농구", "도술"],
       "가족관계": {"#": 2, "아버지": "홍판서", "어머니": "춘섬"},
       "회사": "경기 수원시 팔달구 우만동"
    },
    {
       "이름": "테스트2",
       "나이": 31,
       "성별": "남",
       "주소": "인천광역시 부평구 부펴동",
       "특기": ["게임", "축구"],
       "가족관계": {"#": 2, "아버지": "김남일", "어머니": "김이나"},
       "회사": ""
    }
  ]
  ```

  - JSON 데이터는 변수 명과 Mapping 이 되기 때문에 반드시 변수 명을 동일하게 지정해줘야 한다.

  ```java
  Noname items[] = new Noname()[2];
  items[0] = new Noname();
  items[1] = new Noname();

  // 이름이 없는 이유는
  // 개발자가 임의로 지어줄 수 있다
  Class Noname{
    String 이름;
    int 나이;
    String 성별;
    String 주소;
    String 특기[];
    가족관계 가족관계 = new 가족관계();
    String 회사;
  }

  Class 가족관계{
    int #;
    String 아버지;
    String 어머니;
  }
  ```

  - GSON (JSON Parsing Library)

    - GSON이란 java 에서 제공하는 JSON API 보다 좀 더 효율적이고, 속도가 빠른 Parsing Library 이다.

    - Gradle 설정

    ```Gradle
    compile 'com.google.coe.gson:gson:2.8.2'
    ```

    - 사용 방법

    ```java
    /**
     * GSON Library 를 사용하여, JSON 을 Parsing 하는 메소드 (하나의 문단 을 객체로 변경)
     *
     * @param jsonString
     */
    private void gsonParse(String jsonString){
        userList = new ArrayList<>();
        jsonString = jsonString.substring(2, jsonString.length()-3);
        // 문자열 분리하기
        String array[] = jsonString.split("\\},\\{");
        Gson gson = new Gson();
        for(String item : array) {
            item = "{" + item + "}";
            User user = gson.fromJson(item, User.class);
            userList.add(user);
        }
    }

    /**
     * GSON Library 를 사용하여, JSON 을 Parsing 하는 메소드 (전체 JSON)
     *
     * @param jsonString
     */
    private void simpleGsonParse(String jsonString){
        Type type = new TypeToken<List<User>>(){}.getType();
        Gson gson = new Gson();
        userList = gson.fromJson(jsonString, type);
    }
    ```

  - 참조 : [JSON](https://ko.wikipedia.org/wiki/JSON), [GSON](https://github.com/google/gson), [GSON guide](https://github.com/google/gson/blob/master/UserGuide.md)

### Code Review
____________________________________________________

- MainActivity.java

  - [https://api.github.com/users](https://api.github.com/users) 에서 json String 을 받아와 Parsing 한 후 RecyclerView 에 보여주는 클래스

  - AsyncTask 를 사용하여 HTTP 통신을 한다.

  ```java
  public class MainActivity extends AppCompatActivity {

      private ArrayList userList;

      private RecyclerView recyclerView;
      private ProgressBar progressBar;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
          recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
          progressBar = (ProgressBar)findViewById(R.id.progressBar);

          load();
      }

      private void load(){
          new AsyncTask<Void, Void,String>(){
              @Override
              protected void onPreExecute() {
                  super.onPreExecute();
                  progressBar.setVisibility(View.VISIBLE);
              }

              @Override
              protected String doInBackground(Void... voids) {
                  return Remote.getData("https://api.github.com/users");
              }

              @Override
              protected void onPostExecute(String jsonString) {
                  progressBar.setVisibility(View.GONE);
                  //JSONString 을 Parsing 하여 List 에 넣어둔다.
                  simpleGsonParse(jsonString);
                  setList();
              }

          }.execute();
      }

      /**
       * 수동으로 JSON 을 Parsing 하는 메소드
       *
       * @param jsonString
       */
      private void parse(String jsonString){
          userList = new ArrayList<>();
          // 앞에 문자 2개 없애기 [, {
          jsonString = jsonString.substring(jsonString.indexOf("{")+1);
          // 뒤에 문자 2개 없애기 }, ]
          jsonString = jsonString.substring(0, jsonString.lastIndexOf("}"));

          // 문자열 분리하기
          String array[] = jsonString.split("\\},\\{");

          for(String item : array){
              User user = new User();
              // item 문자열을 분리해서 user 의 변수로 넣는다.
              String subArray[] = item.split(",");

              HashMap<String, String> hashItem = new HashMap<>();

              for(String subItem : subArray){
                  subItem = subItem.substring(subItem.indexOf("\"")+1);
                  String temp[] = subItem.split("\":");
                  if(temp[1].startsWith("\"")){
                      temp[1] = temp[1].substring(1,temp[1].lastIndexOf("\""));
                  }
                  hashItem.put(temp[0], temp[1]);
              }

              user.setId(Integer.parseInt(hashItem.get("id")));
              user.setLogin(hashItem.get("login"));
              user.setAvatar_url(hashItem.get("avatar_url"));

              userList.add(user);
          }
      }

      /**
       * GSON Library 를 사용하여, JSON 을 Parsing 하는 메소드 (하나의 문단)
       *
       * @param jsonString
       */
      private void gsonParse(String jsonString){
          userList = new ArrayList<>();
          jsonString = jsonString.substring(2, jsonString.length()-3);
          // 문자열 분리하기
          String array[] = jsonString.split("\\},\\{");
          Gson gson = new Gson();
          for(String item : array) {
              item = "{" + item + "}";
              User user = gson.fromJson(item, User.class);
              userList.add(user);
          }
      }

      /**
       * GSON Library 를 사용하여, JSON 을 Parsing 하는 메소드 (전체 JSON)
       *
       * @param jsonString
       */
      private void simpleGsonParse(String jsonString){
          Type type = new TypeToken<List<User>>(){}.getType();
          Gson gson = new Gson();
          userList = gson.fromJson(jsonString, type);
      }

      private void setList(){
          ListAdapter listAdapter = new ListAdapter(userList);
          recyclerView.setAdapter(listAdapter);
          recyclerView.setLayoutManager(new LinearLayoutManager(this));
      }
  }
  ```

- ListAdapter.java

  - ImageUrl 을 받아 Glide 로 그리는 클래스

  ```java
  public class ListAdapter extends RecyclerView.Adapter<ListAdapter.Holder> {

      List<User> userList;

      public ListAdapter(List<User> userList) {
          this.userList = userList;
      }

      @Override
      public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
          return new Holder(view);
      }

      @Override
      public void onBindViewHolder(final Holder holder, int position) {
          final User user = userList.get(position);
          holder.setTextId(user.getId()+"");
          holder.setTextLogin(user.getLogin());
          holder.setImageView(user.getAvatar_url());
      }

      @Override
      public int getItemCount() {
          return userList.size();
      }

      public class Holder extends RecyclerView.ViewHolder {

          private TextView textId, textLogin;
          private ImageView imageView;

          public Holder(View itemView) {
              super(itemView);
              textId = itemView.findViewById(R.id.textId);
              textLogin = itemView.findViewById(R.id.textLogin);
              imageView = itemView.findViewById(R.id.imageView);
          }

          public void setTextId(String id) {
              textId.setText(id);
          }

          public void setTextLogin(String login) {
              textLogin.setText(login);
          }

          public void setImageView(final String imageUrl) {
              Glide.with(itemView.getContext())
                      .load(imageUrl)
                      .into(imageView);
              /*
              // ImageUrl 을 HttpURLConnection 을 통해 Bitmap 으로 변환한 후 ImageView 에 출력하는 구역
              new AsyncTask<Void, Void, Bitmap>(){
                  @Override
                  protected void onPreExecute() {
                      super.onPreExecute();
                  }

                  @Override
                  protected Bitmap doInBackground(Void... voids) {
                      return Remote.getImage(imageUrl);
                  }

                  @Override
                  protected void onPostExecute(Bitmap bitmap) {
                      //JSONString 을 Parsing 하여 List 에 넣어둔다.
                      imageView.setImageBitmap(bitmap);
                  }
              }.execute();
              */
          }
      }
  }  
  ```

- Remote.java

  - `HttpURLConnection` 을 통해 직접적인 json 데이터를 불러오는 클래스

  - 또한 imageUrl 을 Bitmap 으로 바꿔주는 클래스

  ```java
  public class Remote {

      public static String getData(String urlString) {
          StringBuilder result = new StringBuilder();
          try {
              // Network 처리
              URL url = new URL(urlString);
              HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
              urlConnection.setRequestMethod("GET");

              // 통신이 성공적인지 체크
              if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                  // 여기서부터는 File 에서 Data 를 가져오는 방식과 동일
                  InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());
                  BufferedReader br = new BufferedReader(isr);

                  String temp = "";
                  while ((temp = br.readLine()) != null) {
                      result.append(temp).append("\n");
                  }

                  br.close();
                  isr.close();

              } else {
                  Log.e("ServerError", urlConnection.getResponseCode() + " , " + urlConnection.getResponseMessage());
              }
              urlConnection.disconnect();
          } catch (Exception e) {
              Log.e("Error", e.toString());
          }
          return result.toString();
      }

      public static Bitmap getImage(String src) {
          Bitmap bitmap = null;
          try {
              // Network 처리
              URL url = new URL(src);
              HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
              urlConnection.setRequestMethod("GET");

              // 통신이 성공적인지 체크
              if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                  // 여기서부터는 File 에서 Data 를 가져오는 방식과 동일
                  InputStream is = urlConnection.getInputStream();
                  bitmap = BitmapFactory.decodeStream(is);

                  is.close();
              } else {
                  Log.e("ServerError", urlConnection.getResponseCode() + " , " + urlConnection.getResponseMessage());
              }
              urlConnection.disconnect();
          } catch (Exception e) {
              Log.e("Error", e.toString());
          }

          return bitmap;
      }
  }
  ```

- User.java

  ```java
  public class User {

      // 위에 3개만 일단 사용
      private int id;
      private String login;
      private String avatar_url;
      private String gravatar_id;
      private String url;
      private String html_url;
      private String followers_url;
      private String following_url;
      private String gists_url;
      private String starred_url;
      private String subscriptions_url;
      private String organizations_url;
      private String repos_url;
      private String events_url;
      private String received_events_url;
      private String type;
      private boolean siteAdmin;

      public User() {
      }

      public int getId() {
          return id;
      }

      public void setId(int id) {
          this.id = id;
      }

      public String getLogin() {
          return login;
      }

      public void setLogin(String login) {
          this.login = login;
      }

      public String getAvatar_url() {
          return avatar_url;
      }

      public void setAvatar_url(String avatar_url) {
          this.avatar_url = avatar_url;
      }
  }
  ```
