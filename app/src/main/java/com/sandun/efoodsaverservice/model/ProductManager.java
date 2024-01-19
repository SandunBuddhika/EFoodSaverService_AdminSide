package com.sandun.efoodsaverservice.model;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.sandun.efoodsaverservice.dao.UserDAO;
import com.sandun.efoodsaverservice.dto.Product;
import com.sandun.efoodsaverservice.entities.User;
import com.sandun.efoodsaverservice.util.AppDatabase;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProductManager {
    private static final String TAG = ProductManager.class.getName();
    private Context context;
    private FirebaseFirestore firestore;
    private SimpleDateFormat dateFormat;
    private FirebaseStorage storage;
    private List<String> imgUUID;
    private User user;
    private View view;
    private WaitAlertPopUp alert;

    public ProductManager(Context context, View view) {
        this.context = context;
        this.view = view;
        this.alert = new WaitAlertPopUp(context);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        new Thread(() -> {
            AppDatabase database = new InternalDB<AppDatabase>(context.getApplicationContext(), AppDatabase.class).build();
            UserDAO userDAO = database.userDAO();
            user = userDAO.getAll().get(0);
        }).start();
        imgUUID = new ArrayList<>();
    }


    public void upload(Product product, Map<String, Uri> imgList) {
        if (imgList != null) {
            for (int i = 0; i < imgList.size(); i++) {
                imgUUID.add(String.valueOf(System.currentTimeMillis()) + UUID.randomUUID().toString() + user.getId());
            }
            if (user != null && productValidator(product)) {
                alert.show();
                dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                product.setCreated_date(dateFormat.format(date));
                product.setuId(user.getId());
                product.setUpdated_date(dateFormat.format(date));
                product.setImgList(imgUUID);
                product.setStatus("active");
                String productId = String.valueOf(System.currentTimeMillis()) + user.getId();
                firestore.collection("product").document(productId).set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "Success");
                        String path = "product/" + productId;
                        int index = 0;
                        for (Uri img : imgList.values()) {
                            uploadFiles(path, img, index);
                            Log.i(TAG, String.valueOf(index));
                            index++;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error: " + e.getMessage());
                    }
                });
            }
        }
    }

    private boolean productValidator(Product product) {

        if (product.getName() == null || product.getName().isEmpty()) {
            validatorAlert("Please Enter Product Name");
            return false;
        } else if (product.getQty() <= 0) {
            validatorAlert("Qty Must be More than 0");
            return false;
        } else if (product.getPrice() <= 0) {
            validatorAlert("Price Must be More than 0");
            return false;
        }else if (product.getCategory() == null || product.getCategory().isEmpty()) {
            validatorAlert("Please Enter Product Category");
            return false;
        } else if (product.getType() == null || product.getType().isEmpty()) {
            validatorAlert("Please Enter Product Type");
            return false;
        } else if (product.getExpire_date() == null || product.getExpire_date().isEmpty()) {
            validatorAlert("Please Select Product Expiration Date");
            return false;
        } else if (product.getManufacture_date() == null || product.getManufacture_date().isEmpty()) {
            validatorAlert("Please Select Product Manufacture Date");
            return false;
        } else if (product.getDescription() == null || product.getDescription().isEmpty()) {
            validatorAlert("Please Enter Product Description");
            return false;
        } else if (imgUUID.size() == 0) {
            validatorAlert("Please At Least Select 1 Image For Your Product");
            return false;
        } else {
            return true;
        }
    }

    public void validatorAlert(String error) {
        alert.customButtonSetUp(new WaitAlertButtonManager() {
            @Override
            public void process(AlertDialog dialog, WaitAlertPopUp alertPopUp) {
                dialog.dismiss();
            }
        });
        alert.changeButtonState(WaitAlertPopUp.VISIBLE_STATE);
        alert.setUp(error, WaitAlertPopUp.ERROR);
        if (alert.isShow) {
            alert.getDialog().show();
        } else {
            alert.show();
        }
    }

    public void uploadFiles(String path, Uri file, int position) {
        alert.setUp("Image " + position + " Started To Upload", WaitAlertPopUp.PROGRESS);
        Log.i(TAG, String.valueOf(position));
        StorageReference reference = storage.getReference(path);
        reference.child(imgUUID.get(position)).putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                alert.customButtonSetUp(new WaitAlertButtonManager() {
                    @Override
                    public void process(AlertDialog dialog, WaitAlertPopUp alertPopUp) {
                        dialog.dismiss();
                        alertPopUp.changeButtonState(WaitAlertPopUp.HIDE_STATE);
                    }
                });
                alert.changeButtonState(WaitAlertPopUp.VISIBLE_STATE);
                alert.setUp("Successfully Product Uploaded!", WaitAlertPopUp.SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alert.customButtonSetUp(new WaitAlertButtonManager() {
                    @Override
                    public void process(AlertDialog dialog, WaitAlertPopUp alertPopUp) {
                        dialog.dismiss();
                    }
                });
                alert.changeButtonState(WaitAlertPopUp.VISIBLE_STATE);
                alert.setUp("Product Uploading Failed!", WaitAlertPopUp.ERROR);
                Log.i(TAG, "Error: " + e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                alert.setUp("Image " + position + " Uploading " + progress + "% ...", WaitAlertPopUp.PROGRESS);
                Log.i(TAG, "Progress: " + progress);
                Log.i(TAG, snapshot.getBytesTransferred() + " Of " + snapshot.getTotalByteCount());
            }
        });
    }
}
