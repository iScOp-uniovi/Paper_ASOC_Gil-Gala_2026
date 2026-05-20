# Open Formats

## 📄 Description

This folder contains open-format versions of the supplementary result files associated with the paper:

> **"Evolving ensembles of routing policies for the electric vehicle routing problem using hyper-heuristic methods"**
>
> Submitted to **Applied Soft Computing Journal**
>
> Authors: Francisco Javier Gil Gala, Marko Đurasević and María Rita Sierra Sánchez
>
> Status: *In review*
>
> Manuscript reference: **ASOC-D-25-12027**

The purpose of this folder is to improve compliance with **FAIR principles** by providing reusable, interoperable and machine-readable versions of the data originally stored in Excel workbooks.

---

## 📂 Folder Structure

```text
open_formats/
└── csv/
    ├── StatisticalAnalysis_19_05_2026/
    └── SupplementaryMaterial_19_05_2026/
```

- `csv/` → Contains CSV exports generated from the Excel workbooks stored in the parent `results/` folder.
- `csv/StatisticalAnalysis_19_05_2026/` → CSV versions of the sheets contained in `StatisticalAnalysis_19_05_2026.xlsx`.
- `csv/SupplementaryMaterial_19_05_2026/` → CSV versions of the sheets contained in `SupplementaryMaterial_19_05_2026.xlsx`.

---

## 🔓 Open-Format Conversion

The original result files are provided in Excel format:

- `StatisticalAnalysis_19_05_2026.xlsx`
- `SupplementaryMaterial_19_05_2026.xlsx`

For interoperability and reuse, each readable worksheet has been exported to a separate CSV file.

Each CSV file corresponds to one worksheet from one of the original Excel workbooks. The directory name identifies the source workbook, and the CSV filename identifies the source sheet.

---

## 📊 Source Workbooks

### `StatisticalAnalysis_19_05_2026.xlsx`

This workbook contains:

- statistical analyses associated with selected figures and one table from the paper;
- intermediate calculations and statistical comparisons;
- data used to support the reported statistical conclusions;
- internal documentation included in the workbook.

The corresponding CSV files are located in:

```text
open_formats/csv/StatisticalAnalysis_19_05_2026/
```

### `SupplementaryMaterial_19_05_2026.xlsx`

This workbook contains:

- data underlying all figures and tables reported in the manuscript;
- supplementary experimental results;
- aggregated metrics and values used to generate visualisations;
- intermediate values supporting the reported results.

The corresponding CSV files are located in:

```text
open_formats/csv/SupplementaryMaterial_19_05_2026/
```

---

## 📑 CSV Files

The CSV files provide open and machine-readable versions of the workbook sheets.

They are intended for:

- automated analysis;
- reproducibility checks;
- independent verification of figures and tables;
- reuse with open tools such as Python, R, Julia or LibreOffice;
- long-term preservation in open-data repositories.

CSV files preserve the tabular content of the original Excel sheets as far as possible. The original Excel files should be considered the source supplementary material, while the CSV files provide interoperable derivatives for reuse.

---

## 🔁 Traceability

Each CSV file can be traced back to:

1. the original Excel workbook;
2. the worksheet from which it was exported;
3. the figure, table or statistical analysis documented in the manuscript or workbook.

The parent `results/README.md` provides the global description of the results folder and the role of each workbook.

---

## 🧪 Reproducibility Notes

To reproduce or verify the results:

1. Consult the original Excel workbook in the parent `results/` folder.
2. Use the corresponding CSV file in this folder for automated processing.
3. Preserve the relationship between workbook, sheet and CSV filename.
4. Use the data together with the experimental instances provided in the `data/` folder.

---

## 📜 License

Unless stated otherwise in the repository, the CSV files and documentation contained in this folder are distributed under the:

**Creative Commons Attribution 4.0 International License (CC BY 4.0)**

---

## 📝 Notes

This repository is associated with a manuscript currently under review. DOI, final citation and repository URI will be updated after publication or archival deposit.
