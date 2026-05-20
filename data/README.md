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

This folder preserves both:

- the **original ZIP files** containing the Schneider-format EVRP instances;
- the **FAIR-enhanced ZIP files**, which add open CSV formats, documentation and checksums.

---

## 📂 Folder Structure

```text
data/
├── README.md
├── SchneiderInstancesTraining.zip
├── SchneiderInstancesTest.zip
├── SchneiderInstancesTraining_FAIR.zip
└── SchneiderInstancesTest_FAIR.zip
```

---

## 📦 Dataset Files

### Original instance archives

#### `SchneiderInstancesTraining.zip`

Original archive containing the training instances used during the learning/evolutionary phase.

- Number of instance files: **47**
- Format: Schneider EVRP plain-text instance files (`*.txt`)

#### `SchneiderInstancesTest.zip`

Original archive containing the independent test instances used for the final experimental evaluation.

- Number of instance files: **45**
- Format: Schneider EVRP plain-text instance files (`*.txt`)

These original archives are preserved unchanged as the source instance files.

---

### FAIR-enhanced instance archives

#### `SchneiderInstancesTraining_FAIR.zip`

FAIR-enhanced version of `SchneiderInstancesTraining.zip`.

It contains:
- the original Schneider-format `.txt` instance files;
- open-format CSV versions of the node tables and parameter tables;
- a `ReadMe_*_data_structure.txt` file describing the structure of the data;
- a `CHECKSUMS.txt` file with SHA-256 checksums;
- an internal `README.md`.

#### `SchneiderInstancesTest_FAIR.zip`

FAIR-enhanced version of `SchneiderInstancesTest.zip`.

It contains:
- the original Schneider-format `.txt` instance files;
- open-format CSV versions of the node tables and parameter tables;
- a `ReadMe_*_data_structure.txt` file describing the structure of the data;
- a `CHECKSUMS.txt` file with SHA-256 checksums;
- an internal `README.md`.

---

## 🧬 Instance Families

Both training and test archives include Schneider EVRP instances from the following families:

- `c*` → clustered customer distribution
- `r*` → random customer distribution
- `rc*` → mixed random-clustered customer distribution

Instance types include:

- `*_21.txt` → full-size Schneider EVRP instances
- `*C5.txt`, `*C10.txt`, `*C15.txt` → reduced-size instances with 5, 10 or 15 customers

---

## 🔍 Instance Format

Each original instance file is a plain-text file describing an EVRP instance. The first part of the file contains the list of depots, charging stations and customers.

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

1. Use `SchneiderInstancesTraining.zip` as the original source for the training instances.
2. Use `SchneiderInstancesTest.zip` as the original source for the test instances.
3. Use `SchneiderInstancesTraining_FAIR.zip` and `SchneiderInstancesTest_FAIR.zip` when open-format CSV versions, checksums or data-structure documentation are required.
4. Preserve the original filenames, since they encode the instance family and size.
5. Report results separately for training and test sets.

---

## 🔐 Integrity Verification

Each FAIR-enhanced archive includes a `CHECKSUMS.txt` file with SHA-256 checksums for all files contained in the archive.

This allows users to verify file integrity and detect accidental modifications or corruption.

---

## 📜 License

Unless stated otherwise in the repository, the data, CSV exports and documentation in this folder are distributed under the **Creative Commons Attribution 4.0 International License (CC BY 4.0)**.

If these datasets are reused, please cite the associated manuscript once the final bibliographic information is available.

---

## 📝 Notes

This repository is associated with a manuscript currently under review. DOI, final citation and repository URI will be updated after publication or archival deposit.
