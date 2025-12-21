package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email"),
           @UniqueConstraint(columnNames = "phone"),
           @UniqueConstraint(columnNames = "cccd_number")
       }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;


    @NotBlank
    @Size(max = 255)
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @NotBlank
    @Size(max = 20)
    @Column(name = "phone", nullable = false, length = 20, unique = true)
    private String phone;

    // New fields: date_of_birth, cccd_number, permanent_address, temporary_address
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 50)
    @Column(name = "cccd_number", nullable = false, length = 50, unique = true)
    private String cccdNumber;

    @Size(max = 255)
    @Column(name = "permanent_address", length = 255)
    private String permanentAddress;

    @Size(max = 255)
    @Column(name = "temporary_address", length = 255)
    private String temporaryAddress;

    // Photo URL - có thể null (chưa upload ảnh)
    @Column(name = "photo_url", columnDefinition = "TEXT", nullable = true)
    private String photoUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "smart_ekyc_enabled", nullable = false)
    private Boolean smartEkycEnabled = false;
    @Column(name =  "face_recognition_enabled", nullable = false)
    private Boolean faceRecognitionEnabled = false;
    @Column(name = "fingerprint_login_enabled", nullable = false)
    private Boolean fingerprintLoginEnabled = false;
    @Column(name = "smat_OTP")
    private String smatOTP;

    // Face embedding for face recognition
    @Column(name = "face_embedding", columnDefinition = "TEXT")
    private String faceEmbedding;

    // FCM Token for push notifications (mỗi user chỉ có 1 device token tại 1 thời điểm)
    @Column(name = "fcm_token", columnDefinition = "TEXT")
    private String fcmToken;

    // Bank relationship - mỗi user chỉ có 1 ngân hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", referencedColumnName = "bank_id")
    private Bank bank;

    // Relationship: one user -> many ekyc photos
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<EkycPhoto> ekycPhotos = new ArrayList<>();

    public enum Role {
        customer,
        officer
    }

    public User() {
        // default constructor
    }

    // Getters and setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCccdNumber() {
        return cccdNumber;
    }

    public void setCccdNumber(String cccdNumber) {
        this.cccdNumber = cccdNumber;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getTemporaryAddress() {
        return temporaryAddress;
    }

    public void setTemporaryAddress(String temporaryAddress) {
        this.temporaryAddress = temporaryAddress;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    // Add getters/setters for smartEkycEnabled, faceRecognitionEnabled and smatOTP
    public Boolean getSmartEkycEnabled() {
        return smartEkycEnabled;
    }

    public void setSmartEkycEnabled(Boolean smartEkycEnabled) {
        this.smartEkycEnabled = smartEkycEnabled;
    }

    public Boolean getFaceRecognitionEnabled() {
        return faceRecognitionEnabled;
    }

    public void setFaceRecognitionEnabled(Boolean faceRecognitionEnabled) {
        this.faceRecognitionEnabled = faceRecognitionEnabled;
    }

    public Boolean getFingerprintLoginEnabled() {
        return fingerprintLoginEnabled;
    }

    public void setFingerprintLoginEnabled(Boolean fingerprintLoginEnabled) {
        this.fingerprintLoginEnabled = fingerprintLoginEnabled;
    }

    public String getSmatOTP() {
        return smatOTP;
    }

    public void setSmatOTP(String smatOTP) {
        this.smatOTP = smatOTP;
    }

    public String getFaceEmbedding() {
        return faceEmbedding;
    }

    public void setFaceEmbedding(String faceEmbedding) {
        this.faceEmbedding = faceEmbedding;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

//    // Ekyc photos accessors
//    public List<EkycPhoto> getEkycPhotos() {
//        return ekycPhotos;
//    }
//
//    public void setEkycPhotos(List<EkycPhoto> ekycPhotos) {
//        this.ekycPhotos = ekycPhotos != null ? ekycPhotos : new ArrayList<>();
//    }
//
//    public void addEkycPhoto(EkycPhoto photo) {
//        ekycPhotos.add(photo);
//        photo.setUser(this);
//    }
//
//    public void removeEkycPhoto(EkycPhoto photo) {
//        ekycPhotos.remove(photo);
//        photo.setUser(null);
//    }

    // Convenience builder-like setters

    public User withPasswordHash(String passwordHash) {
        setPasswordHash(passwordHash);
        return this;
    }

    public User withFullName(String fullName) {
        setFullName(fullName);
        return this;
    }

    public User withEmail(String email) {
        setEmail(email);
        return this;
    }

    public User withPhone(String phone) {
        setPhone(phone);
        return this;
    }

    public User withDateOfBirth(LocalDate dob) {
        setDateOfBirth(dob);
        return this;
    }

    public User withCccdNumber(String cccd) {
        setCccdNumber(cccd);
        return this;
    }

    public User withPermanentAddress(String addr) {
        setPermanentAddress(addr);
        return this;
    }

    public User withTemporaryAddress(String addr) {
        setTemporaryAddress(addr);
        return this;
    }

    public User withPhotoUrl(String url) {
        setPhotoUrl(url);
        return this;
    }

    public User withRole(Role role) {
        setRole(role);
        return this;
    }
}
