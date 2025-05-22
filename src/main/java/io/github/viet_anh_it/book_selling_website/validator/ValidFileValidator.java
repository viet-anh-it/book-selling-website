package io.github.viet_anh_it.book_selling_website.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.github.viet_anh_it.book_selling_website.validator.annotation.ValidFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    @Value("${app.allowed.content-type.image}")
    String allowedContentTypes;

    @Value("${app.allowed.file-extension.image}")
    String allowedFileExtension;

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (file == null || file.isEmpty()) {
            context.buildConstraintViolationWithTemplate("File trống!")
                    .addConstraintViolation();
            return false;
        }

        String uploadedFileContentType = file.getContentType();
        List<String> allowedContentTypeList = List.of(this.allowedContentTypes.split(","));
        if (!allowedContentTypeList.contains(uploadedFileContentType)) {
            context.buildConstraintViolationWithTemplate("File không hợp lệ!")
                    .addConstraintViolation();
            return false;
        }

        @SuppressWarnings("null")
        String cleanFilePath = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = StringUtils.getFilenameExtension(cleanFilePath);
        List<String> allowedFileExtensionList = List.of(this.allowedFileExtension.split(","));
        if (!allowedFileExtensionList.contains(fileExtension)) {
            context.buildConstraintViolationWithTemplate("Đuôi file phải là: " + this.allowedFileExtension)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
