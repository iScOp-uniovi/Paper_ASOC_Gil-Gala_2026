# Data Folder

## 📄 Description

This folder contains the benchmark instances used in the experiments for the paper:

> **"Evolving ensembles of routing policies for the electric vehicle routing problem using hyper-heuristic methods"**
>
> Submitted to **Applied Soft Computing Journal**
>
> Authors: Francisco Javier Gil Gala, Marko Đurasević and María Rita Sierra Sánchez
>
> Status: *In review*
>
> Manuscript reference: **ASOC-D-25-12027**

The datasets are used to train and test hyper-heuristic methods for evolving ensembles of routing policies for the **Electric Vehicle Routing Problem (EVRP)**.

The instances are provided as two compressed files:

- `SchneiderInstancesTraining.zip` → Training instances used during the learning/evolutionary phase.
- `SchneiderInstancesTest.zip` → Test instances used for the final experimental evaluation.

---

## 📂 Folder Structure

```text
data/
├── README.md
├── SchneiderInstancesTraining.zip
└── SchneiderInstancesTest.zip
```

Each ZIP file contains plain-text EVRP instances in Schneider format.

---

## 📦 Dataset Files

### `SchneiderInstancesTraining.zip`

This archive contains the training set used to evolve and tune the routing policies.

- Number of instance files: **47**
- Instance families:
  - `c*` → clustered customer distribution
  - `r*` → random customer distribution
  - `rc*` → mixed random-clustered customer distribution
- Instance types:
  - `*_21.txt` → full-size Schneider EVRP instances
  - `*C5.txt`, `*C10.txt`, `*C15.txt` → reduced-size instances with 5, 10 or 15 customers

### `SchneiderInstancesTest.zip`

This archive contains the independent test set used to assess the final performance of the evolved routing policies.

- Number of instance files: **45**
- Instance families:
  - `c*` → clustered customer distribution
  - `r*` → random customer distribution
  - `rc*` → mixed random-clustered customer distribution
- Instance types:
  - `*_21.txt` → full-size Schneider EVRP instances
  - `*C5.txt`, `*C10.txt`, `*C15.txt` → reduced-size instances with 5, 10 or 15 customers

---

## 🔍 Instance Format

Each instance file is a plain-text file describing an EVRP instance. The first part of the file contains the list of depots, charging stations and customers.

The columns are:

| Column | Description |
|---|---|
| `StringID` | Identifier of the node. Prefix `D` denotes depot, `S` denotes charging station and `C` denotes customer. |
| `Type` | Node type: `d` = depot, `f` = charging station, `c` = customer. |
| `x` | X coordinate of the node. |
| `y` | Y coordinate of the node. |
| `demand` | Customer demand. Depot and charging stations have zero demand. |
| `ReadyTime` | Earliest service start time. |
| `DueDate` | Latest service start time. |
| `ServiceTime` | Service duration at the node. |

After the node table, each instance includes vehicle and operational parameters:

| Parameter | Description |
|---|---|
| `Q` | Vehicle fuel/battery capacity. |
| `C` | Vehicle load capacity. |
| `r` | Fuel or energy consumption rate. |
| `g` | Inverse refuelling/recharging rate. |
| `v` | Average vehicle velocity. |

---

## 🧪 Training and Test Split

The dataset is explicitly divided into:

- **Training instances**, used to evolve, select or tune routing policies.
- **Test instances**, used only for final validation and performance assessment.

This separation is intended to avoid information leakage between the learning phase and the final evaluation phase.

---

## 🔁 Reproducibility

To reproduce the experimental setup:

1. Use the instances in `SchneiderInstancesTraining.zip` during the training or policy-evolution phase.
2. Use the instances in `SchneiderInstancesTest.zip` for the final test evaluation.
3. Preserve the original filenames, since they encode the instance family and size.
4. Report results separately for training and test sets.

---

## 📜 License

Unless stated otherwise in the repository, the data and documentation in this folder are distributed under the **Creative Commons Attribution 4.0 International License (CC BY 4.0)**.

If these datasets are reused, please cite the associated manuscript once the final bibliographic information is available.

---

## 📝 Notes

This repository is associated with a manuscript currently under review. DOI, final citation and repository URI will be updated after publication or archival deposit.
