package migration;

import cluster.Server;
import lombok.Data;
import lombok.NonNull;
import vm.VM;

@Data
public class Migration {
    @NonNull private Server from;
    @NonNull private Server to;
    @NonNull private VM vm;
    @NonNull private VM targetVM;
    @NonNull private int transferredData;
}
