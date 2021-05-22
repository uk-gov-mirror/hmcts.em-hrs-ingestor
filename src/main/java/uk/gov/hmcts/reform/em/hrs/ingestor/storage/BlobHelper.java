package uk.gov.hmcts.reform.em.hrs.ingestor.storage;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItemProperties;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItemSet;

import java.io.OutputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

public class  BlobHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlobHelper.class);

    public static final String NULLMD_5 = "NULLMD5";

    @NotNull
    static String parseFolderFromPath(String filePath) {
        final int separatorIndex = filePath.indexOf("/");
        LOGGER.info("Separator Index {}", separatorIndex);
        String folder = "";
        if (separatorIndex == -1) {
            LOGGER.warn("Invalid Path for filepath {} ", filePath);
        } else {
            folder = filePath.substring(0, separatorIndex);
        }
        LOGGER.info("folder {}", folder);
        return folder;
    }

    static String getMd5Hash(final byte[] digest) {
        if (digest == null) {
            return NULLMD_5;
        }
        return Base64.getEncoder().encodeToString(digest);
    }

}
