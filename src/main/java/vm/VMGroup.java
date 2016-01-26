package vm;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import java.util.Set;

@Data
@ToString(exclude = "vms")
public class VMGroup {

    @NonNull private Integer id;
    @NonNull private Set<VM> vms;
}
