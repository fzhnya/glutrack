import com.example.profile.ApiResponse
import com.example.profile.Biodata
import com.example.profile.GlucoseData
import com.example.profile.ProductResponse
import com.example.profile.User
import com.example.profile.userdata
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/v0/product/{barcode}.json")
    fun getProduct(@Path("barcode") barcode: String): Call<ProductResponse>

    @POST("api/kelompok_4/register.php")
    fun registerUser(@Body user: userdata): Call<ApiResponse>

    @POST("api/kelompok_4/login.php")
    fun loginUser(@Body user: User): Call<ApiResponse>

    // Endpoint untuk mengirimkan biodata
    @POST("api/kelompok_4/save_biodata.php")
    fun saveBiodata(@Body biodata: Biodata): Call<ApiResponse>

    @GET("api/kelompok_4/get_glucose.php") // Endpoint yang sesuai dengan server Anda
    fun getGlucoseData(): Call<List<GlucoseData>> // Sesuaikan dengan struktur respons API
}

