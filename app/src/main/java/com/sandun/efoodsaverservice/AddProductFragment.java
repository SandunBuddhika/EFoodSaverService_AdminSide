package com.sandun.efoodsaverservice;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.sandun.efoodsaverservice.dto.Product;
import com.sandun.efoodsaverservice.model.DateDialog;
import com.sandun.efoodsaverservice.model.ProductManager;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AddProductFragment extends Fragment {
    private static final String TAG = HomeActivity.class.getName();
    private View view;
    private ImageButton currentUsingBtn;
    private ArrayAdapter<String> adapter;
    private int currentImg;
    private ProductManager productManager;
    private Map<String, Uri> imagePathList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product, container, false);
        imagePathList = new HashMap<>();
        String[] categories = {"abc", "xyz"};
        String[] types = {"abc", "xyz"};

        adapter = new ArrayAdapter<>(getContext(), R.layout.category_list, categories);
        AutoCompleteTextView category = view.findViewById(R.id.category);
        category.setAdapter(adapter);

        adapter = new ArrayAdapter<>(getContext(), R.layout.category_list, types);
        AutoCompleteTextView type = view.findViewById(R.id.type);
        type.setAdapter(adapter);

        EditText exgDate = view.findViewById(R.id.exg_date);
        DateDialog dialog = new DateDialog(getChildFragmentManager());
        exgDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.showDatePicker(exgDate);
                }
            }
        });
        EditText mfgDate = view.findViewById(R.id.mfg_date);
        mfgDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.showDatePicker(mfgDate);
                }
            }
        });

        ImageButton imgBtn1 = view.findViewById(R.id.p_image_1);
        OpenImageSelect(imgBtn1);
        ImageButton imgBtn2 = view.findViewById(R.id.p_image_2);
        OpenImageSelect(imgBtn2);
        ImageButton imgBtn3 = view.findViewById(R.id.p_image_3);
        OpenImageSelect(imgBtn3);

        productManager = new ProductManager(view.getContext(), view);
        Button upload = view.findViewById(R.id.product_upload_btn);
        upload.setOnClickListener(v -> {
            String pName = ((EditText) view.findViewById(R.id.p_name)).getText().toString();
            String qtyText = ((EditText) view.findViewById(R.id.qty)).getText().toString();
            int qty = Integer.parseInt((qtyText.isEmpty() ? "0" : qtyText));
            String priceText = ((EditText) view.findViewById(R.id.price)).getText().toString();
            double price = Double.parseDouble((priceText.isEmpty() ? "0" : priceText));
            int discount = Integer.parseInt(((EditText) view.findViewById(R.id.discount)).getText().toString());
            String categoryText = category.getText().toString();
            String typeText = type.getText().toString();
            String mfgDateText = mfgDate.getText().toString();
            String exgDateText = exgDate.getText().toString();
            String description = ((EditText) view.findViewById(R.id.description)).getText().toString();
            productManager.upload(new Product(pName, description, price, discount, qty, categoryText, typeText, mfgDateText, exgDateText), imagePathList);
        });

        return view;
    }

    private void OpenImageSelect(ImageButton imageButton) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUsingBtn = imageButton;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intentActivityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));
            }
        });
    }

    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                imagePathList.put("img" + currentUsingBtn.getId(), result.getData().getData());
                Log.i(TAG, "Image Path: " + imagePathList.get("img" + currentUsingBtn.getId()));
                currentUsingBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Picasso.get().load(imagePathList.get("img" + currentUsingBtn.getId())).fit().centerCrop().into(currentUsingBtn);
            }
        }
    });
}