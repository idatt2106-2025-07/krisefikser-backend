---
name: Default issue template
about: Describe this issue template's purpose here.
title: ''
labels: ''
assignees: ''

---

name: Issue Template
description: Standard issue template for tracking work
body:
  - type: markdown
    attributes:
      value: |
        ## 📝 Description
  - type: textarea
    id: description
    attributes:
      label: Description
      description: What's the issue? Describe clearly and concisely.
      placeholder: Explain the issue here...
    validations:
      required: true
  - type: markdown
    attributes:
      value: |
        ## ✅ Acceptance Criteria
  - type: checkboxes
    id: acceptance-criteria
    attributes:
      label: Acceptance Criteria
      description: What needs to be true for this issue to be considered complete?
      options:
        - label: Criterion 1
        - label: Criterion 2
        - label: Criterion 3
  - type: markdown
    attributes:
      value: |
        ## 🔗 Related
  - type: textarea
    id: related
    attributes:
      label: Related
      description: Link to related issues, PRs, or discussions
      placeholder: "#123, #456"
    validations:
      required: false
  - type: markdown
    attributes:
      value: |
        ## 🧠 Additional Context
  - type: textarea
    id: additional-context
    attributes:
      label: Additional Context
      description: Any other info that could help resolve the issue
      placeholder: Add any other context about the issue here
    validations:
      required: false
