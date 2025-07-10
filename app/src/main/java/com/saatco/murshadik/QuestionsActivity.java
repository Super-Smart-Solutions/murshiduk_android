package com.saatco.murshadik;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.QuestionAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.QAVoteResponse;
import com.saatco.murshadik.api.response.QuestionResponse;
import com.saatco.murshadik.databinding.ActivityQuestionsBinding;
import com.saatco.murshadik.fragments.ModalBottomSheet;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.Question;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionsActivity extends BaseActivity implements QuestionAdapter.OnSelectItemClickListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    CardView floatingActionButton;
    ImageView btnSortByCategory;
    ImageView btnFilterByCategory;
    SearchView searchView;

    QuestionAdapter questionAdapter;

    private final ArrayList<Question> questions = new ArrayList<>();

    int pageNo = 1;
    boolean isLoading = false;
    String filter = "";
    String search = "";
    boolean sort = false;
    boolean isSortClicked = false;

    ArrayList<Item> keywordsList = new ArrayList<>();

    ActivityQuestionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        ToolbarHelper.setToolBar(this, getResources().getString(R.string.ask_general_question), binding.appBar.getRoot());

        //********************* random keywords api ******************//
        getRandomKeywords();
        //********************* load questions by page no api ******************//
        getQuestionsByPage();

        floatingActionButton.setOnClickListener(view -> {

            if (ProfileHelper.hasAccount(getApplicationContext())) {
                if (ProfileHelper.getAccount(getApplicationContext()).isProfileComplete()) {
                    if ((ProfileHelper.getAccount(getApplicationContext()).getRoleId() == 6 && ProfileHelper.getAccount(getApplicationContext()).isApproved()) || ProfileHelper.getAccount(getApplicationContext()).getRoleId() == 5) {
                        Intent intent = new Intent(QuestionsActivity.this, AskQuestionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("keywords", keywordsList);
                        startActivity(intent);
                    } else
                        DialogUtil.showCommonAlert(QuestionsActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.account_pending));
                } else
                    DialogUtil.showCommonAlert(QuestionsActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.complete_profile_msg));
            }
        });


        btnSortByCategory.setOnClickListener(view -> showFilterByCategory());

        btnFilterByCategory.setOnClickListener(view -> showSortByCategory());

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextSize(11);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.isEmpty()) {
                    search = query;
                    isLoading = true;
                    pageNo = 1;
                    filter = "";
                    sort = false;
                    recyclerView.getRecycledViewPool().clear();
                    questions.clear();
                    getQuestionsByPage();
                    KeyboardUtils.hideKeyboard(searchView);
                    return false;
                } else {
                    search = "";
                    isLoading = true;
                    pageNo = 1;
                    filter = "";
                    sort = false;
                    recyclerView.getRecycledViewPool().clear();
                    questions.clear();
                    getQuestionsByPage();
                    KeyboardUtils.hideKeyboard(searchView);

                }

                return false;

            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (query.equals("")) {
                    search = "";
                    isLoading = true;
                    pageNo = 1;
                    filter = "";
                    sort = false;
                    recyclerView.getRecycledViewPool().clear();
                    questions.clear();
                    getQuestionsByPage();
                    KeyboardUtils.hideKeyboard(searchView);
                }
                return false;
            }


        });

        initScrollListener();
        setAdapter();
    }

    void initViews() {
        recyclerView = binding.rvQuestion;
        progressBar = binding.progressBar;
        floatingActionButton = binding.fab;
        btnSortByCategory = binding.layoutSortByCategory;
        btnFilterByCategory = binding.layoutFilterByCategory;
        searchView = binding.searchView;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PrefUtil.getBoolean(getApplicationContext(), "is_question_asked")) {
            pageNo = 1;
            filter = "";
            search = "";
            sort = false;
            questions.clear();
            questionAdapter.notifyDataSetChanged();
            getQuestionsByPage();
        }
    }


    private void initScrollListener() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (isLoading == false) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == questions.size() - 1) {
                        //bottom of list!
                        getQuestionsByPage();
                    }
                }

            }
        });
    }

    private void getQuestionsByPage() {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<QuestionResponse> call = apiInterface.getAllQuestionsByPage("Bearer " + TokenHelper.getToken(), pageNo, search, filter, sort);
        call.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                progressBar.setVisibility(View.GONE);
                try {

                    ArrayList<Question> questionList = response.body().getQuestions();

                    for (Question question : questionList) {
                        questions.add(question);
                    }

                    pageNo = pageNo + 1;
                    isLoading = false;

                    recyclerView.getRecycledViewPool().clear();
                    questionAdapter.notifyDataSetChanged();

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void setAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        questionAdapter = new QuestionAdapter(questions, getApplicationContext(), this);
        recyclerView.setAdapter(questionAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public void onItemClick(View view, int position, Question question) {

        Intent intent = new Intent(QuestionsActivity.this, AnswerActivity.class);
        intent.putExtra("ID", question.getId());
        startActivity(intent);


    }

    @Override
    public void onUpClick(View view, int position, Question question) {
        vote(1, question);
    }

    @Override
    public void onDownClick(View view, int position, Question question) {
        vote(0, question);
    }

    private void vote(int vote, Question question) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<QAVoteResponse> call = apiInterface.questionVote("Bearer " + TokenHelper.getToken(), question.getId(), vote);
        call.enqueue(new Callback<QAVoteResponse>() {
            @Override
            public void onResponse(Call<QAVoteResponse> call, Response<QAVoteResponse> response) {

                try {

                    question.setVoteCount(response.body().getCount());
                    questionAdapter.notifyDataSetChanged();

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<QAVoteResponse> call, Throwable t) {

            }
        });
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    private void showFilterByCategory() {

        String[] categories = {getResources().getString(R.string.top_rated), getResources().getString(R.string.last_question)};


        ModalBottomSheet<String> modalBottomSheet = new ModalBottomSheet.Builder<String>()
                .setTitle(getResources().getString(R.string.select_option))
                .setItems(categories)
                .setListener((position, item) -> {
                    pageNo = 1;
                    sort = position != 1;
                    isSortClicked = true;
                    search = "";
                    questions.clear();
                    questionAdapter.notifyDataSetChanged();
                    getQuestionsByPage();
                })
                .build();

        modalBottomSheet.show(getSupportFragmentManager(), "sort_by_price");

    }

    private void showSortByCategory() {

        String[] categories = {getResources().getString(R.string.planting), getResources().getString(R.string.disease), getString(R.string.all)};

        ModalBottomSheet<String> modalBottomSheet = new ModalBottomSheet.Builder<String>()
                .setTitle(getResources().getString(R.string.select_option))
                .setItems(categories)
                .setListener((position, item) -> {
                    pageNo = 1;
                    if (position == 2)
                        filter = "";
                    else
                        filter = position == 0 ? String.valueOf(Consts.PLANTING) : String.valueOf(Consts.DISEASE);
                    questions.clear();
                    search = "";
                    getQuestionsByPage();
                })
                .build();

        modalBottomSheet.show(getSupportFragmentManager(), "sort_by_price");

    }

    private void getRandomKeywords() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Item>> call = apiInterface.getRandomKeyword("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {

                try {
                    keywordsList = (ArrayList<Item>) response.body();
                } catch (Exception ex) {
                }

            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {

            }
        });

    }

}
