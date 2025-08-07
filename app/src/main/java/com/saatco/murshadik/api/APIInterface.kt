package com.saatco.murshadik.api;

import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.api.response.CalenderResponse;
import com.saatco.murshadik.api.response.CoffeePlantResponse;
import com.saatco.murshadik.api.response.DashboardResponse;
import com.saatco.murshadik.api.response.DashboardResult;
import com.saatco.murshadik.api.response.FileResponse;
import com.saatco.murshadik.api.response.GroupMessageResponse;
import com.saatco.murshadik.api.response.LabReportResponse;
import com.saatco.murshadik.api.response.LabsResponse;
import com.saatco.murshadik.api.response.MarketResponse;
import com.saatco.murshadik.api.response.OTPResponse;
import com.saatco.murshadik.api.response.PackageResponse;
import com.saatco.murshadik.api.response.QAVoteResponse;
import com.saatco.murshadik.api.response.QuestionDetailResponse;
import com.saatco.murshadik.api.response.QuestionResponse;
import com.saatco.murshadik.api.response.RegionResponse;
import com.saatco.murshadik.api.response.UserResponse;
import com.saatco.murshadik.api.response.UserResponseEdit;
import com.saatco.murshadik.model.ComparedProduct;
import com.saatco.murshadik.model.ConsultationAppointments.ConsultationAppointment;
import com.saatco.murshadik.model.FarmVisit.FarmVisit;
import com.saatco.murshadik.model.GuideNotification;
import com.saatco.murshadik.model.ReefComponents.ReefComponent;
import com.saatco.murshadik.model.clinicService.Clinic;
import com.saatco.murshadik.model.clinicService.ClinicAppointment;
import com.saatco.murshadik.model.clinicService.ClinicAppointmentTime;
import com.saatco.murshadik.model.clinicService.Doctor;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.api.response.WeatherNotificationResponse;
import com.saatco.murshadik.api.response.WeatherResponse;
import com.saatco.murshadik.model.Appointment;
import com.saatco.murshadik.model.City;
import com.saatco.murshadik.model.ConsultantRatings;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.Lab;
import com.saatco.murshadik.model.LabReport;
import com.saatco.murshadik.model.Message;
import com.saatco.murshadik.model.Notifications;
import com.saatco.murshadik.model.Product;
import com.saatco.murshadik.model.Question;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.consultantvideos.CategoryDataOfConsultantVideos;
import com.saatco.murshadik.model.consultantvideos.CommentOfConsultantVideo;
import com.saatco.murshadik.model.consultantvideos.VideoDataOfConsultantVideos;
import com.saatco.murshadik.model.consultantvideos.VideoLikesOfConsultantVideos;
import com.saatco.murshadik.model.workersService.Experience;
import com.saatco.murshadik.model.workersService.Job;
import com.saatco.murshadik.model.workersService.JobWithWorker;
import com.saatco.murshadik.model.workersService.Nationality;
import com.saatco.murshadik.model.workersService.Worker;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Bassam
 */

public interface APIInterface {

    /********** GET Packages **********/
    @GET("packages")
    Call<PackageResponse> getPackages();

    /********** GET APP Dashboard **********/
    @GET("murshadik/New_Dashboard")
    Call<DashboardResponse> getDashboard();

    /********** GET Calender Subs **********/
    @GET("murshadik/subcategory/{id}")
    Call<CalenderResponse> getSubCategory(@Path("id") int id);

    /********** GET Category **********/
    @GET("murshadik/category/{id}")
    Call<List<Item>> getCategory(@Path("id") int id);

    /********** Login User **********/
    @GET("murshadik/CheckLogin")
    Call<BaseResponse> loginUser(@Query("username") String mobile, @Query("role_id") int role,
            @Query("country") String country, @Query("app_type") int appType);

    /********** GET Chat Consultants **********/
    @GET("murshadik/consultants")
    Call<List<User>> getConsultants(@Header("Authorization") String authHeader);

    /********** GET Chat Users **********/
    @GET("murshadik/GetAllChats")
    Call<List<User>> getChatUsers(@Header("Authorization") String authHeader);

    /********** Register User **********/
    @FormUrlEncoded
    @POST("register")
    Call<UserResponse> registerForMesiboToken(@Field("phone_number") String mobile);

    /********** Search **********/
    @GET("murshadik/searcharticles")
    Call<CalenderResponse> search(@Query("Search_Data") String search, @Query("Page_No") String pno,
            @Query("catid") int catId);

    /********** GET Dashboard **********/
    @GET("dashboard?logged_in=true")
    Call<List<DashboardResult>> getDashboardLoggedIn(@Query("objectId") String objectId,
            @Query("needFarmers") String farmers);

    /********** Update OneSignal User **********/
    @FormUrlEncoded
    @POST("updateUserOnesignal")
    Call<UserResponse> updateOnesignal(@Field("objectId") String objectId, @Field("oneSignalId") String oneId,
            @Field("appVersion") String appVersion, @Field("lastLocation") String lastLoc);

    /********** Update OneSignal User **********/
    @FormUrlEncoded
    @POST("updateUserQuickblox")
    Call<UserResponse> updateQuickBlox(@Field("objectId") String objectId, @Field("chatId") int chatId);

    /********** update user **********/
    @FormUrlEncoded
    @POST("updateUser")
    Call<UserResponseEdit> updateUser(@Field("objectId") String objectId,
            @Field("prefix") String prefix,
            @Field("first_name") String fname,
            @Field("last_name") String lname,
            @Field("isIos") String ios,
            @Field("country") String country,
            @Field("profile") String profile);

    /********** update user **********/
    @Headers({ "Accept: application/json" })
    @FormUrlEncoded
    @POST("register")
    Call<UserResponse> registerUser(@Field("objectId") String objectId,
            @Field("prefix") String prefix,
            @Field("first_name") String fname,
            @Field("last_name") String lname,
            @Field("phone") String phone,
            @Field("isConsultant") boolean isConsultant,
            @Field("isIos") String ios,
            @Field("oneSignalId") String oneSignal,
            @Field("country") String country,
            @Field("profile") String profile,
            @Field("skills") String skills,
            @Field("region") String region,
            @Field("linked_in") String linkedin,
            @Field("chatId") String chatId);

    /********** update user **********/
    @Headers({ "Accept: application/json" })
    @Multipart
    @POST("upload_pdf")
    Call<FileResponse> uploadFile(@Part MultipartBody.Part file);

    /********** update user rating **********/
    @Headers({ "Accept: application/json" })
    @FormUrlEncoded
    @POST("updateUserRatings")
    Call<UserResponse> updateUserRating(@Field("objectId") String objectId,
            @Field("consultantObjectId") String prefix,
            @Field("ratings") int ratings);

    /********** update user photo **********/
    @Headers({ "Accept: application/json" })
    @Multipart
    @POST("murshadik/UploadAvatar")
    Call<FileResponse> uploadUserPhoto(@Header("Authorization") String authHeader, @Part MultipartBody.Part file);

    /********** GET Appoitments **********/
    @GET("murshadik/GetAllAppointmentbyLabID")
    Call<List<Appointment>> getAppointments(@Header("Authorization") String authHeader, @Query("lab_id") int labId,
            @Query("date") String date);

    /*************** Book appointment ************/
    @POST("murshadik/AppointmentBook")
    Call<BaseResponse> bookAppointment(@Header("Authorization") String authHeader,
            @Query("appointment_id") int appointmentId);

    /********** GET Appoitments **********/
    @GET("murshadik/GetLab")
    Call<List<Lab>> getLabs(@Header("Authorization") String authHeader);

    /********** GET Lab Data **********/
    @GET("murshadik/getLabData")
    Call<LabsResponse> getLabData(@Header("Authorization") String authHeader);

    /********** GET Questions **********/
    @GET("murshadik/GetAllQuestions")
    Call<QuestionResponse> getAllQuestions(@Header("Authorization") String authHeader);

    /********** GET Questions by page count **********/
    @GET("murshadik/GetAllQuestionsByPage")
    Call<QuestionResponse> getAllQuestionsByPage(@Header("Authorization") String authHeader,
            @Query("Page_No") int pageNo, @Query("Search_Data") String search, @Query("Filter_Value") String filter,
            @Query("sort_value") boolean sort_value);

    /********** GET random keywords Questions **********/
    @GET("murshadik/AutoComplete")
    Call<List<Question>> getAutoCompleteKeyword(@Header("Authorization") String authHeader, @Query("word") String word);

    /********** POST Create new Questions **********/
    @Multipart
    @POST("murshadik/CreateNewQuestion")
    Call<BaseResponse> postQuestion(@Header("Authorization") String authHeader, @Part MultipartBody.Part title,
            @Part MultipartBody.Part description, @Part MultipartBody.Part keywords,
            @Part MultipartBody.Part categoryId, @Part ArrayList<MultipartBody.Part> files);

    /********** GET questionVote **********/
    @GET("murshadik/QuestionVote")
    Call<QAVoteResponse> questionVote(@Header("Authorization") String authHeader, @Query("qid") int id,
            @Query("value") int value);

    /********** GET Answer Vote **********/
    @GET("murshadik/AnswerVote")
    Call<QAVoteResponse> answerVote(@Header("Authorization") String authHeader, @Query("aid") int id,
            @Query("value") int value);

    /********** GET question details **********/
    @GET("murshadik/GetQuestionDetail")
    Call<QuestionDetailResponse> getQuestion(@Header("Authorization") String authHeader, @Query("id") int id);

    /********** POST Create Answer **********/
    @Multipart
    @POST("murshadik/CreateNewAnswer")
    Call<BaseResponse> postAnswer(@Header("Authorization") String authHeader, @Part MultipartBody.Part title,
            @Part MultipartBody.Part description, @Part MultipartBody.Part questionId,
            @Part ArrayList<MultipartBody.Part> files);

    /*************** Add Keyword ************/
    @POST("murshadik/AddKeyword")
    Call<BaseResponse> addKeyword(@Header("Authorization") String authHeader, @Query("word") String word);

    /*************** get user appointments ************/
    @GET("murshadik/GetMyLabAppointment")
    Call<List<Appointment>> getUserAppointments(@Header("Authorization") String authHeader);

    /*************** get user appointments ************/
    @GET("murshadik/GetLabReportByID")
    Call<List<LabReport>> getLabReport(@Header("Authorization") String authHeader, @Query("appointment_id") int id);

    /*************** get user appointments ************/
    @GET("murshadik/GetAllMarkets")
    Call<MarketResponse> getMarkets(@Header("Authorization") String authHeader, @Query("regionid") String id);

    /*************** get product prices in other markets ************/
    @GET("murshadik/productPriceInMarkets")
    Call<NewAPIsResponse<ArrayList<ComparedProduct>>> getProductPricesInOtherMarkets(
            @Header("Authorization") String authHeader, @Query("productId") int productId);

    /*************** get market Details ************/
    @GET("murshadik/GetMarketDetails")
    Call<LabReportResponse> getMarketDetail(@Header("Authorization") String authHeader, @Query("market_id") int id);

    /*************** get market Details ************/
    @GET("murshadik/GetProductPriceFor")
    Call<List<Product>> getProductPriceFor(@Header("Authorization") String authHeader,
            @Query("product_id") int productId, @Query("market_id") int marketId, @Query("filter") int filter);

    /********** GET Weather **********/
    @GET("murshadik/getWeatherDataForCity/{id}")
    Call<WeatherResponse> getWeather(@Path("id") String id);

    /********** Check OPT **********/
    @POST("murshadik/CheckOTPNew")
    Call<OTPResponse> checkOTP(@Query("UserName") String mobile, @Query("OTP") String otp);

    /********** Update Profile **********/
    @FormUrlEncoded
    @POST("murshadik/UpdateProfileNew")
    Call<UserResponse> updateProfile(@Header("Authorization") String authHeader, @Field("name") String name,
            @Field("lastname") String lastName, @Field("phone") String phone, @Field("role_id") int roleId,
            @Field("app_type") int appType, @Field("region_id") int regionId, @Field("country") String country,
            @Field("skills") String skills, @Field("profile") String profile, @Field("prefix") String prefix,
            @Field("city_code") String cityCode, @Field("skill_ids") String skillIds, @Field("city_id") int cityId);

    /********** GET Regions **********/
    @GET("murshadik/getregions")
    Call<RegionResponse> getRegions();

    /********** GET Regions **********/
    @GET("murshadik/getRegionWithCities")
    Call<RegionResponse> getRegionsWithCity();

    /*************** subscribe market product ************/
    @GET("murshadik/ProductSubscriber")
    Call<BaseResponse> subscribeProduct(@Header("Authorization") String authHeader, @Query("productid") int productId,
            @Query("marketid") int marketId, @Query("onincrease") int increase);

    /*************** remove subscribe market product ************/
    @GET("murshadik/DeleteProductSubscriber")
    Call<BaseResponse> removeSubscribeProduct(@Header("Authorization") String authHeader,
            @Query("productid") int productId, @Query("marketid") int marketId);

    /*************** update online / offline ************/
    @GET("murshadik/IsOnline")
    Call<BaseResponse> updateOnlineStatus(@Header("Authorization") String authHeader, @Query("value") boolean status);

    /*************** get product types ************/
    @GET("murshadik/GetMarketProductCategories")
    Call<List<Item>> getProductTypes(@Header("Authorization") String authHeader);

    /*************** get product types ************/
    @GET("murshadik/getcities")
    Call<List<City>> getCities();

    /********** Update Profile **********/
    @GET("murshadik/GetUserProfile")
    Call<User> getProfile(@Header("Authorization") String authHeader);

    /********** Update getsubscribersproduct **********/
    @GET("murshadik/getsubscribersproduct")
    Call<List<Product>> getSubscribedProducts(@Header("Authorization") String authHeader);

    /********** Update chatId **********/
    @GET("murshadik/updateChatID/{id}")
    Call<BaseResponse> updateChatId(@Header("Authorization") String authHeader, @Path("id") String id);

    /********** POST Message **********/
    @Multipart
    @POST("murshadik/message")
    Call<Message> sendMessage(@Header("Authorization") String authHeader, @Part MultipartBody.Part message,
            @Part MultipartBody.Part messageType, @Part MultipartBody.Part status, @Part MultipartBody.Part receiverId,
            @Part MultipartBody.Part file);

    /********** Update chatId **********/
    @GET("murshadik/getUserBy/{id}")
    Call<User> getUserBy(@Header("Authorization") String authHeader, @Path("id") String id);

    /********** Update chatId **********/
    @GET("murshadik/getUserByChatId")
    Call<User> getUserByChatId(@Header("Authorization") String authHeader, @Query("id") int id);

    /********** Get Skills **********/
    @GET("murshadik/getskills")
    Call<List<Item>> getSkills();

    /********** Get user Skills **********/
    @GET("murshadik/GetUserSkills")
    Call<List<Item>> getUserSkills(@Header("Authorization") String authHeader);

    /********** add user Skills **********/
    @GET("murshadik/InsertUserSkill")
    Call<BaseResponse> addUserSkill(@Header("Authorization") String authHeader, @Query("skill_id") int id);

    /********** get group messages **********/
    @GET("murshadik/getGroupMessagesBy_V2")
    Call<GroupMessageResponse> getGroupMessages(@Header("Authorization") String authHeader,
            @Query("group_id") int groupId, @Query("page_size") int pageSize, @Query("Page_No") int pageNo,
            @Query("withMembers") boolean withMembers);

    /********** POST group Message **********/
    @Multipart
    @POST("murshadik/groupmessage")
    Call<Message> sendGroupMessage(@Header("Authorization") String authHeader, @Part MultipartBody.Part message,
            @Part MultipartBody.Part messageType, @Part MultipartBody.Part file);

    /********** get group messages **********/
    @GET("murshadik/GetAllConsultantRating")
    Call<List<ConsultantRatings>> getRatings(@Header("Authorization") String authHeader);

    @FormUrlEncoded
    @POST("murshadik/ConsultantRating")
    Call<BaseResponse> updateUserRating(@Header("Authorization") String authHeader,
            @Field("consultant_id") int consultantId,
            @Field("rating") String rating,
            @Field("comment") String comment);

    /********** GET RandomKeyword **********/
    @GET("murshadik/RandomKeyword")
    Call<List<Item>> getRandomKeyword(@Header("Authorization") String authHeader);

    /********** GET All Notifications **********/
    @GET("murshadik/GetAllNotification")
    Call<List<Notifications>> getAllNotification(@Header("Authorization") String authHeader,
            @Query("dateTime") String dateTime);

    /********** GET Weather Notifications **********/
    @GET("murshadik/GetCurrentWeatherNoficationByCityId")
    Call<WeatherNotificationResponse> getWeatherNotification(@Header("Authorization") String authHeader,
            @Query("city_id") String id);

    /********** Delete All Notifications **********/
    @DELETE("murshadik/DeleteAllNotification")
    Call<BaseResponse> deleteAllNotification(@Header("Authorization") String authHeader);

    /********** Delete All Notifications **********/
    @DELETE("murshadik/DeleteAllNotification_byId")
    Call<BaseResponse> deleteNotificationById(@Header("Authorization") String authHeader, @Query("id") int id);

    /********** Cancel appointment **********/
    @GET("murshadik/CancelAppointment")
    Call<BaseResponse> cancelAppointment(@Header("Authorization") String authHeader, @Query("appointment_id") int id);

    /********** EnableNotificationForType **********/
    @FormUrlEncoded
    @POST("murshadik/EnableNotificationForType")
    Call<BaseResponse> enableNotificationForType(@Header("Authorization") String authHeader,
            @Field("type_id") int type_id, @Field("is_enabled") boolean isEnable);

    /********** COffee plant disease **********/
    @POST("predict/")
    Call<CoffeePlantResponse> getCoffeeDisease(@Body RequestBody params);

    /********** EnableNotificationForType **********/
    @FormUrlEncoded
    @POST("murshadik/updatecallstatus")
    Call<BaseResponse> updateCallStatus(@Header("Authorization") String authHeader, @Field("to_user") int to_user,
            @Field("call_id") String callId, @Field("status") int status, @Field("type") int type);

    /********** updateApp verison **********/
    @FormUrlEncoded
    @POST("murshadik/updateAppVersion")
    Call<BaseResponse> updateAppVersion(@Header("Authorization") String authHeader, @Field("appVersion") String version,
            @Field("lastLocation") String location, @Field("appType") int type);

    /********** updatePhone number **********/
    @POST("murshadik/changePhoneno")
    Call<BaseResponse> changePhoneNumber(@Header("Authorization") String authHeader,
            @Query("newPhoneNo") String number);

    /********** updatePhone while verifying otp **********/
    @POST("murshadik/updatePhoneNoCheckingOTP")
    Call<OTPResponse> updatePhoneNoCheckingOTP(@Header("Authorization") String authHeader,
            @Query("newPhoneNo") String number, @Query("OTP") String OTP);

    /********** get video categories of consultant videos **********/
    @GET("murshadik/video/GetCategories")
    Call<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> getVideoCategories(
            @Header("Authorization") String authHeader);

    /********** get own consultant videos **********/
    @GET("murshadik/video/GetGallery")
    Call<NewAPIsResponse<ArrayList<VideoDataOfConsultantVideos>>> getConsultantVideosGallery(
            @Header("Authorization") String authHeader);

    /********** get all videos **********/
    @GET("murshadik/video/GetAllVideos")
    Call<NewAPIsResponse<ArrayList<VideoDataOfConsultantVideos>>> getAllConsultantVideos(
            @Header("Authorization") String authHeader, @Query("categoryId") String id);

//    /********** get video clip by id **********/
//    @GET("murshadik/video/GetVideo")
//    Call<NewAPIsResponse<VideoDataOfConsultantVideos>> getVideoClipById(@Header("Authorization") String authHeader,
//            @Query("Id") String id);

    /********** get video categories of consultant videos **********/
    @Multipart
    @POST("murshadik/video/PostVideo")
    Call<NewAPIsResponse<String>> uploadVideoClip(@Header("Authorization") String authHeader,
            @Part MultipartBody.Part categoryId, @Part MultipartBody.Part departmentId, @Part MultipartBody.Part title,
            @Part MultipartBody.Part video, @Part MultipartBody.Part description, @Part MultipartBody.Part tag);

    /********** get video categories of consultant videos **********/
    @PUT("Murshadik/video/PutVideo")
    Call<NewAPIsResponse<String>> updateVideoClipData(@Header("Authorization") String authHeader,
            @Query("Id") String videoId, @Query("Title") String title, @Query("Description") String description,
            @Query("DepartmentId") String departmentId, @Query("CategoryId") String categoryId,
            @Query("Tage") String tag, @Query("IsHidden") boolean isHidden);

    /********** post comment on consultant videos **********/
    @Multipart
    @POST("murshadik/videoComment/AddComment")
    Call<NewAPIsResponse<String>> postComment(@Header("Authorization") String authHeader,
            @Part MultipartBody.Part videoId, @Part MultipartBody.Part comment,
            @Part MultipartBody.Part parentCommentId);

    /********** post comment on consultant videos **********/
    @Multipart
    @POST("Murshadik/videoLike/AddLike")
    Call<NewAPIsResponse<String>> postLike(@Header("Authorization") String authHeader, @Part MultipartBody.Part videoId,
            @Part MultipartBody.Part likeStatus);

    /********** get comment of video clip by id **********/
    @GET("murshadik/videoComment/GetComment")
    Call<NewAPIsResponse<ArrayList<CommentOfConsultantVideo>>> getCommentsOfVideo(
            @Header("Authorization") String authHeader, @Query("videoId") String id);

    /********** get Likes of video clip by id **********/
    @GET("Murshadik/videoLike/GetLike")
    Call<NewAPIsResponse<VideoLikesOfConsultantVideos>> getLikesOfVideo(@Header("Authorization") String authHeader,
            @Query("VideoId") String id);

    /********** post Guide notification **********/
    @Multipart
    @POST("Murshadik/GuidedGuidesAlerts/PostGuideAlert")
    Call<NewAPIsResponse<String>> postGuideNotification(@Header("Authorization") String authHeader,
            @Part MultipartBody.Part GuideAlert, @Part MultipartBody.Part region_id, @Part MultipartBody.Part cities,
            @Part MultipartBody.Part skills);

    /********** get Guide notification History **********/
    @GET("Murshadik/GuidedGuidesAlerts/GetGuidAlert")
    Call<NewAPIsResponse<ArrayList<GuideNotification>>> getGuideNotificationsHistory(
            @Header("Authorization") String authHeader);

    /********** post Farm Visit request **********/
    @Multipart
    @POST("Murshadik/FarmVisitOrder/PostFarmVisitOrder")
    Call<NewAPIsResponse<String>> postFarmVisitRequest(@Header("Authorization") String authHeader,
            @Part MultipartBody.Part FarmName, @Part MultipartBody.Part RegionId, @Part MultipartBody.Part CityId,
            @Part MultipartBody.Part PurposeOfVisit, @Part MultipartBody.Part Location,
            @Part MultipartBody.Part VisitDate, @Part ArrayList<MultipartBody.Part> files);

    /********** get all Farm Visit requests **********/
    @GET("Murshadik/FarmVisitOrder/get")
    Call<NewAPIsResponse<ArrayList<FarmVisit>>> getFarmVisitRequests(@Header("Authorization") String authHeader);

    /********** get Delete account **********/
    @GET("Murshadik/removeAccount")
    Call<NewAPIsResponse<String>> deleteAccount(@Header("Authorization") String authHeader);


    /********** Workers service Apis **********/
    @GET("Murshadik/worker/GetNationalties")
    Call<NewAPIsResponse<ArrayList<Nationality>>> getAllNationalities(@Header("Authorization") String authHeader);

    @GET("Murshadik/worker/GetAllWorker")
    Call<NewAPIsResponse<ArrayList<Worker>>> getAllWorkersByJobId(@Header("Authorization") String authHeader,
            @Query("JobId") int jobId);

    @GET("Murshadik/worker/GetAllWorkerByPage")
    Call<NewAPIsResponse<ArrayList<Worker>>> getAllWorkerByPage(@Header("Authorization") String authHeader,
            @Query("pageSize") int pageSize, @Query("pageNumber") int pageNumber);

    @GET("Murshadik/worker/getWorkerjobs")
    Call<NewAPIsResponse<ArrayList<Job>>> getAllWorkerJobs(@Header("Authorization") String authHeader);

    @GET("Murshadik/worker/GetWorker")
    Call<NewAPIsResponse<Worker>> getWorkerById(@Header("Authorization") String authHeader,
            @Query("workerId") int workerId);

    @GET("Murshadik/worker/GetWorkerByUserId")
    Call<NewAPIsResponse<Worker>> getCurrentWorker(@Header("Authorization") String authHeader);

    /**
     * @param body should contain json data with keys:(Phone,
     *             IDNumber,
     *             Address,
     *             Age,
     *             Isbusy,
     *             ExpectedSalary,
     *             NationaltyId,
     *             UserId)
     */
    @POST("Murshadik/worker/AddWorker")
    Call<NewAPIsResponse<Worker>> addWorker(@Header("Authorization") String authHeader,
            @Body RequestBody body);

    @POST("Murshadik/worker/AddWorkerJob")
    Call<NewAPIsResponse<JobWithWorker>> addWorkerJob(@Header("Authorization") String authHeader,
            @Query("jobId") int jopId,
            @Query("workerId") int workerId);

    @DELETE("Murshadik/worker/DeleteWorkerJob")
    Call<NewAPIsResponse<ArrayList<Object>>> deleteWorkerJob(@Header("Authorization") String authHeader,
            @Query("WorkerJobId") int jopWithWorkerId);

    /**
     * @param body should contain json data with keys ("WorkerId", "Descriptin",
     *             "FromDate","ToDate")
     */
    @POST("Murshadik/worker/AddWorkerExperienas")
    Call<NewAPIsResponse<Experience>> addWorkerExperience(@Header("Authorization") String authHeader,
            @Body RequestBody body);

    /**
     * @param body should contain json data with keys:(Id,
     *             Phone,
     *             IDNumber,
     *             Address,
     *             Age,
     *             Isbusy,
     *             ExpectedSalary,
     *             NationaltyId
     */
    @PUT("Murshadik/worker/UpdateWorker")
    Call<NewAPIsResponse<Worker>> updateWorker(@Header("Authorization") String authHeader,
            @Body RequestBody body);

    @DELETE("Murshadik/worker/DeleteWorkerExperienas")
    Call<NewAPIsResponse<Object>> deleteWorkerExperience(@Header("Authorization") String authHeader,
            @Query("experienceId") int experienceId);


    /********** Clinic service Apis **********/
    @GET("Murshadik/Clinic/getClinics")
    Call<NewAPIsResponse<ArrayList<Clinic>>> getClinics(
            @Header("Authorization") String authHeader);

    @GET("Murshadik/Clinic/getAllDoctorsOfClinic")
    Call<NewAPIsResponse<ArrayList<Doctor>>> getAllDoctorsOfClinic(
            @Header("Authorization") String authHeader, @Query("clinic_id") int clinicId);

    @GET("Murshadik/Clinic/getAvailableAppointment")
    Call<NewAPIsResponse<ArrayList<ClinicAppointmentTime>>> getAvailableAppointment(
            @Header("Authorization") String authHeader,
            @Query("date") String date,
            @Query("clinic_id") int clinicId);

    @GET("Murshadik/Clinic/getAvailableAppointmentOfDoctor")
    Call<NewAPIsResponse<ArrayList<ClinicAppointmentTime>>> getAvailableAppointmentOfDoctor(
            @Header("Authorization") String authHeader,
            @Query("date") String date,
            @Query("clinic_id") int clinicId,
            @Query("doctor_id") int doctorId);

    @GET("Murshadik/Clinic/getIsAppointmentTimeUp")
    Call<NewAPIsResponse<String>> getServerTime(
            @Header("Authorization") String authHeader);


    @POST("Murshadik/Clinic/postBookAppointment")
    Call<NewAPIsResponse<Object>> postBookAppointment(
            @Header("Authorization") String authHeader,
            @Query("appointment_id") int appointmentId,
            @Query("booking_reason") String bookingReason,
            @Query("clinic_id") int clinicId);


    @GET("Murshadik/Clinic/getMyAppointments")
    Call<NewAPIsResponse<ArrayList<ClinicAppointment>>> getMyAppointments(
            @Header("Authorization") String authHeader,
            @Query("is_doctor") boolean isDoctor);

    @GET("Murshadik/Clinic/getAppoinmetsHistory")
    Call<NewAPIsResponse<ArrayList<ClinicAppointment>>> getAppointmentsHistory(
            @Header("Authorization") String authHeader,
            @Query("is_doctor") boolean isDoctor);


    @PUT("Murshadik/Clinic/putAppointmentCallDone")
    Call<NewAPIsResponse<String>> putAppointmentCallDone(
            @Header("Authorization") String authHeader,
            @Query("appointment_id") int appointmentId,
            @Query("call_duration") int callDuration);

    @POST("Murshadik/Clinic/postClinicDoctorRating")
    Call<NewAPIsResponse<String>> postClinicDoctorRating(
            @Header("Authorization") String authHeader,
            @Query("clinic_id") int clinicId,
            @Query("doctor_id") int doctorId,
            @Query("comment") String comment,
            @Query("rating") int rating);

    @PUT("Murshadik/Clinic/putCancelApointmentBooking")
    Call<NewAPIsResponse<Object>> putCancelAppointmentBooking(
            @Header("Authorization") String authHeader,
            @Query("AppointmentId") int appointmentId);


    @GET("Murshadik/Clinic/isDoctor")
    Call<NewAPIsResponse<Object>> getIsDoctor(
            @Header("Authorization") String authHeader);


    // =========== Reef Component APIs ===========
    @GET("Murshadik/Reef/getReefComponentSuggestionsById")
    Call<NewAPIsResponse<ArrayList<ReefComponent>>> getReefComponentSuggestionsById(
            @Header("Authorization") String authHeader,
            @Query("id") int id);

    @GET("MurshadiK/Reef/getReefComponentList")
    Call<NewAPIsResponse<ArrayList<ReefComponent>>> getReefComponentList(
            @Header("Authorization") String authHeader);


    // =========== Consultant Appointment APIs ===========
    @GET("MurshadiK/ConsultAppointment/GetAvailableTimes")
    Call<NewAPIsResponse<ArrayList<String>>> cAGetAvailableTimes(
            @Header("Authorization") String authHeader,
            @Query("consultatntId") int consultantId,
            @Query("date") String date);

    @POST("MurshadiK/ConsultAppointment/postBookAppointment")
    Call<NewAPIsResponse<ConsultationAppointment>> cAPostBookAppointment(
            @Header("Authorization") String authHeader,
            @Query("consultantId") int consultantId,
            @Query("skillId") int skillId,
            @Query("bookingReason") String bookingReason,
            @Query("date") String date,
            @Query("time") String time);

    @GET("MurshadiK/ConsultAppointment/GetMyNextConsultationAppointments")
    Call<NewAPIsResponse<ArrayList<ConsultationAppointment>>> cAGetMyNextConsultationAppointments(
            @Header("Authorization") String authHeader);


    @GET("MurshadiK/ConsultAppointment/GetMyHistoryConsultationAppointments")
    Call<NewAPIsResponse<ArrayList<ConsultationAppointment>>> cAGetMyHistoryConsultationAppointments(
            @Header("Authorization") String authHeader);



    @PUT("MurshadiK/ConsultAppointment/PutAppointmentCallDone")
    Call<NewAPIsResponse<String>> cAPutAppointmentCallDone(
            @Header("Authorization") String authHeader,
            @Query("appointmentId") int appointmentId,
            @Query("callDuration") int callDuration);



    @PUT("MurshadiK/ConsultAppointment/PutAppointmentCallStartBy")
    Call<NewAPIsResponse<String>> cAPutAppointmentCallStartBy(
            @Header("Authorization") String authHeader,
            @Query("appointmentId") int appointmentId);


    @PUT("MurshadiK/ConsultAppointment/PutCancelAppointment")
    Call<NewAPIsResponse<String>> cAPutCancelAppointment(
            @Header("Authorization") String authHeader,
            @Query("appointmentId") int appointmentId);

    @PUT("MurshadiK/ConsultAppointment/PutCancelAppointment")
    Call<String> cancelConsultationAppointment(@Query("appointmentId") String appointmentId);

    @Multipart
    @POST("images/uploads")
    Call<com.saatco.murshadik.models.UploadImageResponse> uploadImage(
            @Header("Authorization") String authHeader,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part name,
            @Part MultipartBody.Part plantId,
            @Part MultipartBody.Part farmId,
            @Part MultipartBody.Part annotated
    );

    @POST("inferences")
    Call<com.saatco.murshadik.models.InferenceResponse> createInference(
            @Header("Authorization") String authHeader,
            @Query("image_id") int imageId
    );

    @POST("inferences/{id}/validate")
    Call<com.saatco.murshadik.models.InferenceResponse> validateInference(
            @Header("Authorization") String authHeader,
            @Path("id") int id
    );

    @POST("inferences/{id}/detect")
    Call<com.saatco.murshadik.models.InferenceResponse> detectDisease(
            @Header("Authorization") String authHeader,
            @Path("id") int id
    );

    @GET("diseases/{id}")
    Call<com.saatco.murshadik.models.Disease> getDiseaseById(
            @Header("Authorization") String authHeader,
            @Path("id") int id
    );


}

