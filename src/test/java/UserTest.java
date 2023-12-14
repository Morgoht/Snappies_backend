import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import lombok.SneakyThrows;
import org.example.models.User;
import org.example.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import

        static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class UserTest {


    @Mock
    private CollectionReference colRef;
    @Mock
    private DocumentReference docRef;

    @Mock
    private ApiFuture<DocumentSnapshot> future;

    @Mock
    private DocumentSnapshot document;

    @SneakyThrows
    @Test
    public void userByIdTest() {
        Firestore mockFirestore = Mockito.mock(Firestore.class);
        Mockito.when(mockFirestore.collection("users")).thenReturn(colRef);
        Mockito.when(colRef.document("user1")).thenReturn(docRef);
        User mockUser = new User();
        Mockito.when(docRef.get()).thenReturn(future);
        Mockito.when(future.get()).thenReturn(document);
        Mockito.when(document.exists()).thenReturn(true);
        Mockito.when(document.toObject(User.class)).thenReturn(mockUser);

        UserService userService = new UserService(mockFirestore);
        User user;
        try {
            user = userService.userById("user1");
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Assert that the retrieved user is equal to the mocked user
        assertThat(user).isEqualTo(mockUser);
    }
}